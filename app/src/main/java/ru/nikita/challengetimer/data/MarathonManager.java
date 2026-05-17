package ru.nikita.challengetimer.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class MarathonManager {
    private static final String PREFS = "marathon_state_v3"; // ↑ версия для миграции
    private static final String KEY_ACTIVE_DAYS = "active_days";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_COMPLETED = "completed_days";
    private static final String KEY_ATTEMPTS = "attempts_"; // префикс для попыток

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static int getActiveDays(Context c) { return prefs(c).getInt(KEY_ACTIVE_DAYS, 0); }
    public static long getStartTime(Context c) { return prefs(c).getLong(KEY_START_TIME, 0L); }
    public static boolean hasActive(Context c) { return getActiveDays(c) > 0; }

    public static void startMarathon(Context c, int days) {
        // Если стартуем тот же марафон — увеличиваем попытку
        if (getActiveDays(c) == days) {
            incrementAttempt(c, days);
        } else if (getActiveDays(c) != 0) {
            // Если был другой активный — его сбрасываем (опционально)
            prefs(c).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
        }
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

    /// Система попыток
    public static int getAttemptCount(Context c, int days) {
        return prefs(c).getInt(KEY_ATTEMPTS + days, 0);
    }

    public static void incrementAttempt(Context c, int days) {
        int current = prefs(c).getInt(KEY_ATTEMPTS + days, 0);
        prefs(c).edit().putInt(KEY_ATTEMPTS + days, current + 1).apply();
    }

    public static void resetAttempt(Context c, int days) {
        prefs(c).edit().putInt(KEY_ATTEMPTS + days, 0).apply();
    }

    /// Прерывание марафона
    public static void abortMarathon(Context c) {
        prefs(c).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
    }

    public static void resetMarathonCompletely(Context c, int days) {
        // Удаляем из пройденных (если был пройден)
        Set<String> completed = new HashSet<>(prefs(c).getStringSet(KEY_COMPLETED, new HashSet<>()));
        completed.remove(String.valueOf(days));
        prefs(c).edit().putStringSet(KEY_COMPLETED, completed).apply();
        // Сбрасываем попытку
        resetAttempt(c, days);
        // Сбрасываем активный
        abortMarathon(c);
    }
}