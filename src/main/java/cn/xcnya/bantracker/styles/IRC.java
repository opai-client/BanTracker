package cn.xcnya.bantracker.styles;

import cherryhikari.utils.LoggerWithOpai;
import com.google.gson.JsonObject;
import today.opai.api.OpenAPI;

public class IRC implements TrackerStyle {
    private final LoggerWithOpai logger;

    public IRC(OpenAPI openAPI) {
        this.logger = new LoggerWithOpai(openAPI, "§bBan Tracker §7>>");
        logger.enableTimeLog(false);
    }

    @Override
    public void print(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        String msg = String.format("§7[§dIRC§7] §fWD §a+%d §7| §fST §c+%d", wdDiff, stDiff);
        String hover = String.format("§8IRC Tracker\n§fWD total §7→ §a%d\n§fST total §7→ §c%d",
                lastWD + wdDiff, lastST + stDiff);

        logger.infoWithHover(hover, msg);
    }
}