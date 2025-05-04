package cherryhikari.utils;

import com.google.gson.JsonObject;
import today.opai.api.OpenAPI;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class LoggerWithOpai {
    OpenAPI api = null;
    String prefix = null;
    public LoggerWithOpai(OpenAPI api,String prefix) {
        this.api = api;
        this.prefix = prefix;
    }

    public void info(String... msgs){
        infoWithHover(null, Arrays.toString(msgs));
    }

    public void infoWithHover(String hover,String... msgs){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StringBuilder builder = new StringBuilder()
                .append("§6[").append(sdf.format(new Date())).append("]§r ") // 主播对不起我有强迫症
                .append(prefix).append(" §r");
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
