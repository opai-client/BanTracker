package cn.xcnya.bantracker.styles;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import cherryhikari.utils.LoggerWithOpai;

public class Default implements TrackerStyle {
    private LoggerWithOpai logger = null;

    public Default() {
        this.logger = logger;
    }

    @Override
    public void log(String hover, String msg) {
        logger.infoWithHover(hover, msg); // 默认样式使用 infoWithHover
    }

    @Override
    public String getMessage(int wdDiff, int stDiff, JsonObject data) {
        String msg = "";
        if (wdDiff > 0) msg += "§fWatchdog: §a+" + wdDiff + " ";
        if (stDiff > 0) msg += "§fStaff: §c+" + stDiff;
        return msg.trim();
    }

    @Override
    public String getHover(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        int watchdog = data.getAsJsonObject("watchdog").get("total").getAsInt();
        int staff    = data.getAsJsonObject("staff").get("total").getAsInt();
        int lastMin  = data.getAsJsonObject("watchdog").get("last_minute").getAsInt();
        int halfHour = data.getAsJsonObject("staff").get("last_half_hour").getAsInt();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String hover = String.format("§6[%s]§r §4Hypixel BanTracker\n", sdf.format(new Date()));
        hover += String.format("§fWatchdog Total: §c%d §7→ §a%d §5(+%d)\n", lastWD, watchdog, wdDiff);
        hover += String.format("§fStaff Total:    §c%d §7→ §a%d §5(+%d)\n", lastST, staff, stDiff);
        hover += String.format("§fWatchdog Last Minute: §a%d\n", lastMin);
        hover += String.format("§fStaff  Half Hour:    §c%d", halfHour);
        return hover;
    }
}