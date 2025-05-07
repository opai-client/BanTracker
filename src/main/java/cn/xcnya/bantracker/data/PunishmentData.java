package cn.xcnya.bantracker.data;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * 没有人喜欢下划线命名的。
 *
 * @author renshengongji
 * @date 2025/5/8 04:58
 **/
public class PunishmentData {
    public Staff staff = new Staff();
    public Watchdog watchdog = new Watchdog();
    public List<History> history = Collections.emptyList();

    //staff和watchdog的field都不同，如果一样用一个class就可以了
    public static class Staff {
        @SerializedName("last_half_hour") public int lastHalfHour = 0;
        @SerializedName("last_day") public int lastDay = 0;
        public int total = 0;
    }
    public static class Watchdog {
        @SerializedName("last_minute") public int lastMinute = 0;
        @SerializedName("last_day") public int lastDay = 0;
        public int total = 0;
    }
    public static class History {
        public long time;
        @SerializedName("formated") public String formattedTime; // formated?
        public boolean watchdog; // boolean是坏习惯
        @SerializedName("number") public int count;
    }
}
