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
    public Staff staff;
    public Watchdog watchdog;
    @Deprecated public List<History> history; // 你说得对但是 0个用法 //hehe

    //staff和watchdog的field都不同，如果一样用一个class就可以了 //怪我？
    public static class Staff {
        @SerializedName("last_half_hour") public int lastHalfHour;
        @Deprecated @SerializedName("last_day") public int lastDay;
        public int total;
    }
    public static class Watchdog {
        @SerializedName("last_minute") public int lastMinute;
        @Deprecated @SerializedName("last_day") public int lastDay;
        public int total;
    }

    public static class History {
        @Deprecated public long time;
        @Deprecated @SerializedName("formated") public String formattedTime; // formated?
        @Deprecated public boolean watchdog; // boolean是坏习惯 //i like
        @Deprecated @SerializedName("number") public int count;
    }
}
