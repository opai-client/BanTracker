package cn.xcnya.bantracker.utils;

import com.google.gson.JsonObject;
import today.opai.api.OpenAPI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerWithOpai {
    private volatile boolean timeLogEnabled = true;
    private final OpenAPI api;
    private final String prefix;

    public LoggerWithOpai(OpenAPI api, String prefix) {
        this.api = api;
        this.prefix = prefix;
    }

    // 新增开关控制方法
    public void enableTimeLog(boolean enabled) {
        this.timeLogEnabled = enabled;
    }

    @Deprecated
    public void info(String... msgs) {
        infoWithHover(null, msgs);
    }

    public void infoWithHover(String hover, String... msgs) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StringBuilder builder = new StringBuilder();

        if (timeLogEnabled) {
            builder.append("§6[")
                    .append(sdf.format(new Date()))
                    .append("]§r ");
        }

        builder.append(prefix)
                .append(" §r");

        for (String s : msgs) {
            builder.append(s).append(" ");
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", builder.toString());

        if (hover != null) {
            JsonObject hoverObject = new JsonObject();
            hoverObject.addProperty("action", "show_text");
            hoverObject.addProperty("value", hover);
            jsonObject.add("hoverEvent", hoverObject);
        }

        api.printChatComponent(jsonObject.toString());
    }
}