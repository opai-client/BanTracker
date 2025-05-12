package cn.xcnya.bantracker.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 没有人喜欢下划线命名的。
 *
 * @author renshengongji
 * @date 2025/5/8 04:58
 **/
public class PunishmentData {
    public Punishment staff;
    public Punishment watchdog;
    public List<History> history;

    public static class Punishment {
        @SerializedName("last_minute") public int lastMinute = 0;
        @SerializedName("last_half_hour") public int lastHalfHour = 0;
        @SerializedName("last_day") public int lastDay = 0;
        public int total = 0;
    }

    public static class History {
        public long time;
        @SerializedName("formated") public String formattedTime;
        public boolean watchdog;
        @SerializedName("number") public int count;
    }
}
