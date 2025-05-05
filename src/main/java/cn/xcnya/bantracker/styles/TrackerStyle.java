package cn.xcnya.bantracker.styles;

import com.google.gson.JsonObject;

public interface TrackerStyle {
    void print(int wdDiff, int stDiff, int lastWD, int lastST, JsonObject data);
}
