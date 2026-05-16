package ru.nikita.challengetimer.common;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class MarathonManager {
    private static final String PREFS = "marathon_state_v2";
    private static final String KEY_ACTIVE_DAYS = "active_days";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_COMPLETED = "completed_days";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static int getActiveDays(Context c) { return prefs(c).getInt(KEY_ACTIVE_DAYS, 0); }
    public static long getStartTime(Context c) { return prefs(c).getLong(KEY_START_TIME, 0L); }
    public static boolean hasActive(Context c) { return getActiveDays(c) > 0; }

    public static void startMarathon(Context c, int days) {
        prefs(c).edit()
                .putInt(KEY_ACTIVE_DAYS, days)
                .putLong(KEY_START_TIME, System.currentTimeMillis())
                .apply();
    }

    public static void completeMarathon(Context c, int days) {
        Set<String> completed = new HashSet<>(prefs(c).getStringSet(KEY_COMPLETED, new HashSet<>()));
        completed.add(String.valueOf(days));
        prefs(c).edit().putStringSet(KEY_COMPLETED, completed).apply();
        // Сбрасываем активный
        prefs(c).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
    }

    public static boolean isCompleted(Context c, int days) {
        return prefs(c).getStringSet(KEY_COMPLETED, new HashSet<>()).contains(String.valueOf(days));
    }
}
