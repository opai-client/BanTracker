package cn.xcnya.bantracker.styles;

import cn.xcnya.bantracker.data.PunishmentData;

public interface TrackerStyle {
    void print(int wdDiff, int stDiff, int lastWD, int lastST, PunishmentData data);
}
