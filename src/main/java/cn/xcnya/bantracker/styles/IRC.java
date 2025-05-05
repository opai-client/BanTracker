package cn.xcnya.bantracker.styles;

import com.google.gson.JsonObject;

public class IRC implements TrackerStyle {
    @Override
    public String getMessage(int wdDiff, int stDiff, JsonObject data) {
        return String.format("§7[§dIRC§7] §fWD §a+%d §7| §fST §c+%d", wdDiff, stDiff);
    }

    @Override
    public String getHover(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        int watchdog = data.getAsJsonObject("watchdog").get("total").getAsInt();
        int staff    = data.getAsJsonObject("staff").get("total").getAsInt();
        return String.format("§8IRC Tracker\n§fWD total §7→ §a%d\n§fST total §7→ §c%d", watchdog, staff);
    }
}