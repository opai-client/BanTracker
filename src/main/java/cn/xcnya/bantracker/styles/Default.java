package cn.xcnya.bantracker.styles;

import cherryhikari.utils.LoggerWithOpai;
import com.google.gson.JsonObject;
import today.opai.api.OpenAPI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Default implements TrackerStyle {
    private final LoggerWithOpai logger;

    public Default(OpenAPI openAPI) {
        this.logger = new LoggerWithOpai(openAPI, "§cBanTracker §7→");
    }

    @Override
    public void print(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        String msg = "";
        if (wdDiff > 0) msg += "§fWatchdog: §a+" + wdDiff + " ";
        if (stDiff > 0) msg += "§fStaff: §c+" + stDiff;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        String hover = String.format("§6[%s]§r §4Hypixel BanTracker\n", sdf.format(new Date())) +
                String.format("§fWatchdog Total: §c%d §7→ §a%d §5(+%d)\n", lastWD, lastWD + wdDiff, wdDiff) +
                String.format("§fStaff Total:    §c%d §7→ §a%d §5(+%d)\n", lastST, lastST + stDiff, stDiff) +
                String.format("§fWatchdog Last Minute: §a%d\n", data.getAsJsonObject("watchdog").get("last_minute").getAsInt()) +
                String.format("§fStaff  Half Hour:    §c%d", data.getAsJsonObject("staff").get("last_half_hour").getAsInt());

        logger.infoWithHover(hover, msg.trim());
    }
}