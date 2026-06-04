package ru.nikita.challengetimer.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class MarathonManager {
    private static final String PREFS = "marathon_state_v3"; // ↑ версия для миграции
    private static final String KEY_ACTIVE_DAYS = "active_days";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_COMPLETED = "completed_days";
    private static final String KEY_ATTEMPTS = "attempts_"; // префикс для попыток

    private static final int[] ALL_CHALLENGES = {1, 3, 5, 7, 10, 14, 21, 30};

    private static  final String[] SUBTITLES = {
            "Первый шаг",
            "Новый фундамент",
            "Ритм привычки",
            "Неделя силы",
            "Декада прогресса",
            "Полмесяца дисциплины",
            "Формирование привычки",
            "Месяц трансформации"
    };

    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static int getActiveDays(Context context) {
        return prefs(context).getInt(KEY_ACTIVE_DAYS, 0);
    }

    public static long getStartTime(Context context) {
        return prefs(context).getLong(KEY_START_TIME, 0L);
    }

    public static boolean hasActive(Context context) {
        return getActiveDays(context) > 0;
    }

    public static void startMarathon(Context context, int days) {
        // Если стартуем тот же марафон — увеличиваем попытку
        if (getActiveDays(context) == days) {
            incrementAttempt(context, days);
        } else if (getActiveDays(context) != 0) {
            // Если был другой активный — его сбрасываем (опционально)
            prefs(context).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
        }
        prefs(context).edit()
                .putInt(KEY_ACTIVE_DAYS, days)
                .putLong(KEY_START_TIME, System.currentTimeMillis())
                .apply();
    }

    public static void completeMarathon(Context context, int days) {
        Set<String> completed = new HashSet<>(prefs(context).getStringSet(KEY_COMPLETED, new HashSet<>()));
        completed.add(String.valueOf(days));
        prefs(context).edit().putStringSet(KEY_COMPLETED, completed).apply();
        // Сбрасываем активный
        prefs(context).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
    }

    public static boolean isCompleted(Context context, int days) {
        return prefs(context).getStringSet(KEY_COMPLETED, new HashSet<>()).contains(String.valueOf(days));
    }

    public static int[] getChallenges() {
        return ALL_CHALLENGES;
    }

    public static String[] getChallengeSubtitle() {
        return SUBTITLES;
    }


    /// Система попыток
    public static int getAttemptCount(Context context, int days) {
        return prefs(context).getInt(KEY_ATTEMPTS + days, 0);
    }

    public static void incrementAttempt(Context context, int days) {
        int current = prefs(context).getInt(KEY_ATTEMPTS + days, 0);
        prefs(context).edit().putInt(KEY_ATTEMPTS + days, current + 1).apply();
    }

    public static void resetAttempt(Context context, int days) {
        prefs(context).edit().putInt(KEY_ATTEMPTS + days, 0).apply();
    }

    /// Прерывание марафона
    public static void abortMarathon(Context context) {
        prefs(context).edit().putInt(KEY_ACTIVE_DAYS, 0).putLong(KEY_START_TIME, 0).apply();
    }

    public static void resetMarathonCompletely(Context context, int days) {
        // Удаляем из пройденных (если был пройден)
        Set<String> completed = new HashSet<>(prefs(context).getStringSet(KEY_COMPLETED, new HashSet<>()));
        completed.remove(String.valueOf(days));
        prefs(context).edit().putStringSet(KEY_COMPLETED, completed).apply();
        // Сбрасываем попытку
        resetAttempt(context, days);
        // Сбрасываем активный
        abortMarathon(context);
    }
}