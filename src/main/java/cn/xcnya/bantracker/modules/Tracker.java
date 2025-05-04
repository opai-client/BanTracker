package cn.xcnya.bantracker.modules;

import cherryhikari.utils.LoggerWithOpai;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import static cn.xcnya.bantracker.BanTracker.openAPI;

public class Tracker extends ExtensionModule implements EventHandler {
    public static Tracker INSTANCE;
    private Timer timer;
    private int lastWatchdog = -1;
    private int lastStaff = -1;

    private final LoggerWithOpai logger = new LoggerWithOpai(openAPI,"§cBanTracker §7→");

    public Tracker() {
        super("BanTracker", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        setEventHandler(this);
        INSTANCE = this;
    }

    private Optional<JsonObject> trackerPunishment(){
        try {
            URL url = new URL("https://api.plancke.io/hypixel/v1/punishmentStats");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            JsonParser parser = new JsonParser();  // 呵呵，为什么用 2.2.4 害我
            if (conn.getResponseCode() != 200){
                return Optional.empty();
            }
            JsonObject json = parser.parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            return Optional.of(json);

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
            JsonObject json = punishment.get();

            JsonObject record = json.getAsJsonObject("record");
            int watchdog = record.get("watchdog_rollingDaily").getAsInt();
            int staff = record.get("staff_rollingDaily").getAsInt();

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
                            .append(sdf.format(new Date())).append("]§r ")   // 主播对不起我有强迫症
                            .append("§4Hypixel BanTracker Built In Game").append('\n');
                    HoverMessage.append(String.format("§fWatchdog Daily: §c%d §7→ §a %d§r §5+ %d", lastWatchdog, watchdog, wdDiff))
                            .append('\n');
                    HoverMessage.append(String.format("§fStaff Daily: §c%d §7→ §a %d§r §5+ %d", lastStaff, staff, stDiff));

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

    @Override
    public void onDisabled() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean isHidden() {
        // 隐藏不是一个好习惯！
        return super.isHidden();
    }
}