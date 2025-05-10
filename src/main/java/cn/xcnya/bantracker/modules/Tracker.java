package cn.xcnya.bantracker.modules;

import cn.xcnya.bantracker.data.PunishmentData;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.values.ModeValue;

import cn.xcnya.bantracker.styles.*;

import java.util.*;

import static cn.xcnya.bantracker.BanTracker.openAPI;
import static cn.xcnya.bantracker.BanTracker.versionID;

public class Tracker extends ExtensionModule implements EventHandler {
    public static final Gson gson = new Gson();
    public static Tracker INSTANCE;
    private Timer timer;

    private int lastWatchdog = -1;
    private int lastStaff = -1;
    private final HashMap<String, TrackerStyle> stylesMap = new HashMap<>();
    private final ModeValue styles;
    {
        stylesMap.put("Default", new Default(openAPI));
        stylesMap.put("Opai IRC", new IRC(openAPI));
        stylesMap.put("Funny", new Funny(openAPI));
        stylesMap.put("Island", new Island(openAPI));
        styles = openAPI.getValueManager().createModes("Display Style", "Default", stylesMap.keySet().toArray(new String[0]));
    }

    private final HashMap<String, String> remoteApis = new HashMap<>();
    private final ModeValue apis;
    {
        remoteApis.put("sakuraniroku","https://bantracker.23312355.xyz");
        remoteApis.put("niko233","https://bantracker.niko233.me");
        remoteApis.put("libxcnya","https://bantracker-api.xcnya.cn");

        String[] remotes = remoteApis.keySet().toArray(new String[0]);

        apis = openAPI.getValueManager().createModes("Source", "sakuraniroku", remotes);

    }


    private final OkHttpClient client = new OkHttpClient();

    public Tracker() {
        super("Ban Tracker\n", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        super.addValues(styles,apis);
        setEventHandler(this);
        INSTANCE = this;
    }

    private PunishmentData trackerPunishment(){
        try {
            Request request = new Request.Builder()
                    .url(remoteApis.get(apis.getValue()))
                    .header("User-Agent", String.format("Mozilla/9.0 (Opai Client v%s)", openAPI.getClientVersion()))
                    .build();

            try(Response response = client.newCall(request).execute()){
                if (response.code() != 200) return null;
                return gson.fromJson(response.body().string(), PunishmentData.class);
            }

        } catch (Exception e) {
            openAPI.printMessage("§4Exception " + e.getLocalizedMessage());
            return null;
        }
    }


    private TrackerStyle getStyleInstance(String mode) {
        return stylesMap.get(mode);
    }

    private void trackBans() {
        try {
            PunishmentData punishmentData = trackerPunishment();
            if (punishmentData == null) {
                Queue<String> availableApis = requestAvailableApis();
                while (!availableApis.isEmpty()){
                    apis.setValue(availableApis.poll());
                    punishmentData = trackerPunishment();
                    if (punishmentData != null) break;
                }

                if (punishmentData == null) {
                    disableModule();
                    return;
                }
                openAPI.printMessage(String.format("§e[Ban Tracker] API 不可用，自动切换至%s。", apis.getValue()));

            }

            // 我眼睛瞎了，别害我
            setSuffix(String.format("%d %d", punishmentData.watchdog.lastMinute, punishmentData.staff.lastHalfHour));

            int watchdog = punishmentData.watchdog.total;
            int staff    = punishmentData.staff.total;

            if (lastWatchdog != -1 && lastStaff != -1) {
                int wdDiff = watchdog - lastWatchdog;
                int stDiff = staff    - lastStaff;

                if (wdDiff > 0 || stDiff > 0) {
                    TrackerStyle style = getStyleInstance(styles.getValue());
                    style.print(wdDiff, stDiff, lastWatchdog, lastStaff, punishmentData);
                }
            }

            lastWatchdog = watchdog;
            lastStaff = staff;

        } catch (Exception ignored) {
        }
    }

    private Queue<String> requestAvailableApis(){
        Queue<String> tryingApis = new LinkedList<>();
        for (String mode : apis.getAllModes()) {
            if (mode.equals(apis.getValue())) continue;
            tryingApis.offer(mode);
        }
        return tryingApis;
    }

    @Override
    public void onEnabled() {
        new Thread(() -> {
            boolean initSuccess = false;
            Queue<String> tryingApis = requestAvailableApis();

            if (trackerPunishment() == null){
                while (!tryingApis.isEmpty()){
                    apis.setValue(tryingApis.poll());
                    if (trackerPunishment() != null) {
                        initSuccess = true;
                        openAPI.printMessage(String.format("§e[Ban Tracker] API 不可用，自动切换至%s。", apis.getValue()));
                        break;
                    }
                }
            } else initSuccess = true;


            if (!initSuccess) {
                disableModule();
                return;
            }

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    trackBans();
                }
            }, 0, 5000);
        }, "BanTracker-Init").start();
    }

    private void disableModule(){
        openAPI.printMessage("§e[Ban Tracker] API 不可用，模块已关闭。");
        setEnabled(false);
    }

    public void disableTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void onDisabled() {
        setSuffix(versionID);
        disableTimer();
    }
}