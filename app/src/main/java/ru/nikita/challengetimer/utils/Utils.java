package ru.nikita.challengetimer.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Utils {

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