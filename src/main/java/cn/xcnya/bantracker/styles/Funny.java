package cn.xcnya.bantracker.styles;

import com.google.gson.JsonObject;

public class Funny implements TrackerStyle {
    @Override
    public String getMessage(int wdDiff, int stDiff, JsonObject data) {
        String msg = "";
        if (wdDiff > 0) msg += "§e(ง •̀_•́)ง §a+" + wdDiff + " bots! ";
        if (stDiff > 0) msg += "§d(╯°□°）╯︵ §c+" + stDiff + " humans!";
        return msg.trim();
    }

    @Override
    public String getHover(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data) {
        return "§b今日份制裁统计！加油！ヾ(≧▽≦*)o";
    }
}
