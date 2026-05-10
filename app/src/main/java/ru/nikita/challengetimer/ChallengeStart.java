package ru.nikita.challengetimer;

import java.util.Calendar;

public class ChallengeStart {

    public static long getHardcodedStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MAY, 11, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}