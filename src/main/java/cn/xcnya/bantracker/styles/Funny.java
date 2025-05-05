package cn.xcnya.bantracker.styles;

import cherryhikari.utils.LoggerWithOpai;
import com.google.gson.JsonObject;
import today.opai.api.OpenAPI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Funny implements TrackerStyle {
    private final LoggerWithOpai watchdogLogger;
    private final LoggerWithOpai staffLogger;

    public Funny(OpenAPI openAPI) {
        this.watchdogLogger = new LoggerWithOpai(openAPI, "§e[WATCHDOG]");
        this.staffLogger    = new LoggerWithOpai(openAPI, "§6[STAFF]");

        watchdogLogger.enableTimeLog(false);
        staffLogger.enableTimeLog(false);
    }

    @Override
    public void print(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        if (wdDiff <= 0 && stDiff <= 0) return;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if (wdDiff > 0) {
            String msg = "§e刚刚有 " + wdDiff + " 个人上门报警，被狗咬死了。";
            String hover = String.format("§6[%s]§r §e来自 Watchdog 的封禁\n§f前: §c%d §7→ §a%d §5(+%d)",
                    sdf.format(new Date()), lastWD, lastWD + wdDiff, wdDiff);
            watchdogLogger.infoWithHover(hover, msg);
        }

        if (stDiff > 0) {
            String msg = "§6刚刚有 " + stDiff + " 个人上门报警，被帽子逮捕了。";
            String hover = String.format("§6[%s]§r §d来自 Staff 的封禁\n§f前: §c%d §7→ §a%d §5(+%d)",
                    sdf.format(new Date()), lastST, lastST + stDiff, stDiff);
            staffLogger.infoWithHover(hover, msg);
        }
    }
}