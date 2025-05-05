package cn.xcnya.bantracker.modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.values.ModeValue;

import cn.xcnya.bantracker.styles.*;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static cn.xcnya.bantracker.BanTracker.openAPI;

public class Tracker extends ExtensionModule implements EventHandler {
    public static Tracker INSTANCE;
    private Timer timer;

    private int lastWatchdog = -1;
    private int lastStaff = -1;
    private JsonObject punishmentData = new JsonObject();
    private final ModeValue Mode = openAPI.getValueManager().createModes("Display Style", "Default", new String[]{"Default", "Opai IRC", "Funny"});

    private final OkHttpClient client = new OkHttpClient();

    public Tracker() {
        super("BanTracker", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        super.addValues(Mode);
        setEventHandler(this);
        INSTANCE = this;
    }

    private Optional<JsonObject> trackerPunishment(){
        try {
            Request request = new Request.Builder()
                    .url("https://bantracker.qxiao.eu.org/")
                    .build();

            try(Response response = client.newCall(request).execute()){
                if (response.code() != 200) return Optional.empty();
                return Optional.of(new JsonParser().parse(response.body().string()).getAsJsonObject());
            }

        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private boolean testAPI() {
        return trackerPunishment().isPresent();
    }

    private TrackerStyle getStyleInstance(String mode) {
        switch (mode) {
            case "Opai IRC":
                return new IRC(openAPI);
            case "Funny":
                return new Funny(openAPI);
            case "Default":
            default:
                return new Default(openAPI);
        }
    }

    private void trackBans() {
        try {
            Optional<JsonObject> opt = trackerPunishment();
            if (!opt.isPresent()) return;

            punishmentData = opt.get();

            int watchdog = punishmentData.getAsJsonObject("watchdog").get("total").getAsInt();
            int staff    = punishmentData.getAsJsonObject("staff").get("total").getAsInt();

            if (lastWatchdog != -1 && lastStaff != -1) {
                int wdDiff = watchdog - lastWatchdog;
                int stDiff = staff    - lastStaff;

                if (wdDiff > 0 || stDiff > 0) {
                    TrackerStyle style = getStyleInstance(Mode.getValue());
                    style.print(wdDiff, stDiff, lastWatchdog, lastStaff, punishmentData);
                }
            }

            lastWatchdog = watchdog;
            lastStaff = staff;

        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();

        new Thread(() -> {
            if (!testAPI()) {
                openAPI.getModuleManager().getModule("BanTracker").setEnabled(false);
                openAPI.printMessage("§e[Ban Tracker] API 不可用，模块已关闭。");
                return;
            }

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    trackBans();
                }
            }, 0, 5000);
        }, "BanTracker-Init").start();
    }

    public void disableTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void onDisabled() {
        disableTimer();
    }

    @Override
    public String getSuffix() {
        try{
            return String.format("%d %d",
                    punishmentData.getAsJsonObject("watchdog").get("last_minute").getAsInt(),
                    punishmentData.getAsJsonObject("staff").get("last_half_hour").getAsInt());
        }catch (Exception ignored){
            return super.getSuffix();
        }
    }
}