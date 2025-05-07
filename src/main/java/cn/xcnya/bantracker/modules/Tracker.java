package cn.xcnya.bantracker.modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.values.ModeValue;

import cn.xcnya.bantracker.styles.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
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

    public Tracker() {
        super("Ban Tracker", "Trace Hypixel Bans by Staff/Watchdog", EnumModuleCategory.MISC);
        super.addValues(Mode);
        setEventHandler(this);
        INSTANCE = this;
    }

    private Optional<JsonObject> trackerPunishment() {
        HttpURLConnection connection = null;
        try {
            // 创建URL对象
            URL url = new URL("https://bantracker.niko233.me");

            // 打开连接
            connection = (HttpURLConnection) url.openConnection();

            // 如果是HTTPS连接，进行额外的安全设置
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                try {
                    // 设置使用TLS 1.2
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, null, null);
                    httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                } catch (NoSuchAlgorithmException e) {
                    openAPI.printMessage("§e[Ban Tracker] 不支持TLS 1.2: " + e.getMessage());
                } catch (Exception e) {
                    openAPI.printMessage("§e[Ban Tracker] 设置SSL失败: " + e.getMessage());
                }
            }

            // 设置请求方法和属性
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("User-Agent", "BanTracker/1.6");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 获取响应状态
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                openAPI.printMessage("§e[Ban Tracker] " + responseCode + " " + connection.getResponseMessage());
                return Optional.empty();
            }

            // 读取响应内容
            BufferedReader reader;
            if ("gzip".equals(connection.getContentEncoding())) {
                // 如果是gzip压缩，使用GZIPInputStream解压
                reader = new BufferedReader(new InputStreamReader(
                        new java.util.zip.GZIPInputStream(connection.getInputStream()),
                        StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return Optional.of(new JsonParser().parse(response.toString()).getAsJsonObject());

        } catch (IOException e) {
            openAPI.printMessage("§e[Ban Tracker] 网络请求失败: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            openAPI.printMessage("§e[Ban Tracker] 解析数据失败: " + e.getMessage());
            return Optional.empty();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
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