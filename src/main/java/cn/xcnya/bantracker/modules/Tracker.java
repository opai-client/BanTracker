package cn.xcnya.bantracker.modules;

import cherryhikari.utils.LoggerWithOpai;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.xcnya.bantracker.BanTracker.openAPI;

public class Tracker extends ExtensionModule implements EventHandler {
    public static Tracker INSTANCE;
    private Timer timer;

    private int lastWatchdog = -1;
    private int lastStaff = -1;
    private JsonObject punishmentData = new JsonObject();

    private final OkHttpClient client = new OkHttpClient();

    private final LoggerWithOpai logger = new LoggerWithOpai(openAPI,"§cBanTracker §7→");

    public Tracker() {
        super("BanTracker", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        setEventHandler(this);
        INSTANCE = this;
    }

    private Optional<JsonObject> trackerPunishment(){
        try {
            Request request = new Request.Builder()
                    .url("https://bantracker.qxiao.eu.org/")
                    .build();

            try(Response response = client.newCall(request).execute()){
                if (response.code() != 200) return Optional.empty();
                return Optional.of(new JsonParser().parse(response.body().string()).getAsJsonObject());
            }

        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private boolean testAPI() {
        return trackerPunishment().isPresent();
    }

    private void trackBans() {
        try {
            Optional<JsonObject> punishment = trackerPunishment();
            if (!punishment.isPresent())
                return;
            punishmentData = punishment.get();

            int watchdog = punishmentData.getAsJsonObject("watchdog").get("total").getAsInt();
            int staff = punishmentData.getAsJsonObject("staff").get("total").getAsInt();

            if (lastWatchdog != -1 && lastStaff != -1) {
                int wdDiff = watchdog - lastWatchdog;
                int stDiff = staff - lastStaff;
                if (wdDiff > 0 || stDiff > 0) {
                    StringBuilder trackerMsg = new StringBuilder();
                    if (wdDiff > 0)
                        trackerMsg.append("§fWatchdog: §a").append(wdDiff).append(" ");
                    if (stDiff > 0)
                        trackerMsg.append("§fStaff: §c").append(stDiff);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                    StringBuilder HoverMessage = new StringBuilder("§6[")
                            .append(sdf.format(new Date())).append("]§r ")
                            .append("§4Hypixel BanTracker Built In Game").append('\n');
                    HoverMessage.append(String.format("§fWatchdog Total: §c%d §7→ §a %d§r §5+ %d", lastWatchdog, watchdog, wdDiff))
                            .append('\n');
                    HoverMessage.append(String.format("§fStaff Total: §c%d §7→ §a %d§r §5+ %d", lastStaff, staff, stDiff)).append("§r").append('\n');

                    HoverMessage.append(String.format("§fWatchdog Last Minute: §c%d\n", punishmentData.getAsJsonObject("watchdog").get("last_minute").getAsInt()));
                    HoverMessage.append(String.format("§fStaff Within Half Hour: §a%d", punishmentData.getAsJsonObject("staff").get("last_half_hour").getAsInt()));

                    logger.infoWithHover(HoverMessage.toString(), trackerMsg.toString());
                }
            }

            lastWatchdog = watchdog;
            lastStaff = staff;

        } catch (Exception ignored) {

        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();

        new Thread(() -> {
            if (!testAPI()) {
                openAPI.getModuleManager().getModule("BanTracker").setEnabled(false);
                logger.info("API 不可用，模块已关闭。");
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

    public void disableTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void onDisabled() {
        disableTimer();
    }

    @Override
    public boolean isHidden() {
        // 隐藏不是一个好习惯！
        return super.isHidden();
    }

    @Override
    public String getSuffix() {
        return Integer.toString(punishmentData.getAsJsonObject("watchdog").get("last_minute").getAsInt());
    }
}