package ru.nikita.challengetimer.common;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Calendar;

public class ChallengeStart {

    public static long getHardcodedStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MAY, 11, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getElapsedDays() {
        return Math.max(0, (System.currentTimeMillis() - getHardcodedStart()) / (24L * 60 * 60 * 1000));
    }

    public static long getElapsedMillis() {
        return Math.max(0, System.currentTimeMillis() - getHardcodedStart());
    }

    @NonNull
    @Contract(pure = true)
    public static String formatDays(long days) {
        long mod10 = days % 10;
        long mod100 = days % 100;
        if (mod100 >= 11 && mod100 <= 19) return days + " дней";
        if (mod10 == 1) return days + " день";
        if (mod10 >= 2 && mod10 <= 4) return days + " дня";
        return days + " дней";
    }
}