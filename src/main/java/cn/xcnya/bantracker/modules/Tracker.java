package cn.xcnya.bantracker.modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.OpenAPI;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Tracker extends ExtensionModule implements EventHandler {
    public static Tracker INSTANCE;
    public static OpenAPI openAPI;
    private Timer timer;
    private int lastWatchdog = -1;
    private int lastStaff = -1;

    public Tracker() {
        super("BanTracker", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        setEventHandler(this);
        INSTANCE = this;
    }

    private boolean testAPI() {
        try {
            URL url = new URL("https://api.plancke.io/hypixel/v1/punishmentStats");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            return json.has("success") && json.get("success").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    private void trackBans() {
        try {
            URL url = new URL("https://api.plancke.io/hypixel/v1/punishmentStats");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            if (!json.get("success").getAsBoolean()) return;

            JsonObject record = json.getAsJsonObject("record");
            int watchdog = record.get("watchdog_rollingDaily").getAsInt();
            int staff = record.get("staff_rollingDaily").getAsInt();

            if (lastWatchdog != -1 && lastStaff != -1) {
                int wdDiff = watchdog - lastWatchdog;
                int stDiff = staff - lastStaff;
                if (wdDiff > 0 || stDiff > 0) {
                    openAPI.printMessage("§cBanTracker §7→ " +
                            (wdDiff > 0 ? ("§fWatchdog: §a+" + wdDiff + " ") : "") +
                            (stDiff > 0 ? ("§fStaff: §c+" + stDiff) : ""));
                }
            }

            lastWatchdog = watchdog;
            lastStaff = staff;

        } catch (Exception ignored) {
            // 呵呵
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();

        if (!testAPI()) {
            openAPI.printMessage("§cBanTracker §7→ §cAPI 不可用，模块已关闭。");
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                trackBans();
            }
        }, 0, 5000);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}