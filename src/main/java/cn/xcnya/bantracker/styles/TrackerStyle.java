package cn.xcnya.bantracker.styles;

import com.google.gson.JsonObject;

public interface TrackerStyle {
    String getMessage(int wdDiff, int stDiff, JsonObject data);
    String getHover(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data);
    void log(String hover, String msg);
}
