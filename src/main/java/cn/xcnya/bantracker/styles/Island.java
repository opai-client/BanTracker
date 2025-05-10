package cn.xcnya.bantracker.styles;

import cn.xcnya.bantracker.data.PunishmentData;
import today.opai.api.OpenAPI;

import today.opai.api.enums.EnumNotificationType;

public class Island implements TrackerStyle {

    private final OpenAPI openAPI;

    public Island(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @Override
    public void print(int wdDiff, int stDiff, int lastWD, int lastST, PunishmentData data) {
        if (wdDiff <= 0 && stDiff <= 0) return;
        if (!openAPI.getGameStateManager().isHypixel()) return;
        // 妊娠公鸡你为什么害我翻了2小时的原始码

        if (wdDiff > 0) {
            String msg = String.format("§eWatchdog§r banned %d Player(s).", wdDiff);
            openAPI.popNotification(EnumNotificationType.WARNING, "Ban Tracker", msg, 3000);
        }

        if (stDiff > 0) {
            String msg = String.format("§6Staff§r banned %d Player(s).", stDiff);
            openAPI.popNotification(EnumNotificationType.WARNING, "Ban Tracker", msg, 3000);
        }
    }
}