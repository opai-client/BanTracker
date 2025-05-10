package cn.xcnya.bantracker.styles;

import cn.xcnya.bantracker.data.PunishmentData;
import today.opai.api.OpenAPI;

import java.text.SimpleDateFormat;
import today.opai.api.enums.EnumNotificationType;

public class Island implements TrackerStyle {

    private final OpenAPI openAPI;

    public Island(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @Override
    public void print(int wdDiff, int stDiff, int lastWD, int lastST, PunishmentData data) {
        if (wdDiff <= 0 && stDiff <= 0) return;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if (wdDiff > 0) {
            String msg = "§eWatchdog§r banned " + wdDiff + " Player(s).";
            openAPI.popNotification(EnumNotificationType.WARNING, "Ban Tracker", msg, 3000);
        }

        if (stDiff > 0) {
            String msg = "§6Staff§r banned " + stDiff + " Player(s).";
            openAPI.popNotification(EnumNotificationType.WARNING, "Ban Tracker", msg, 3000);
        }
    }
}