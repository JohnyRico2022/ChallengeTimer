package ru.nikita.challengetimer.database;

import androidx.room.TypeConverter;
import java.time.DayOfWeek;

import ru.nikita.challengetimer.note.PartOfDay;

public class Converters {
    @TypeConverter
    public static String fromDayOfWeek(DayOfWeek day) {
        return day == null ? null : day.name();
    }

    @TypeConverter
    public static DayOfWeek toDayOfWeek(String dayName) {
        return dayName == null ? null : DayOfWeek.valueOf(dayName);
    }

    @TypeConverter
    public static String fromPartOfDay(PartOfDay part) {
        return part == null ? null : part.name();
    }

    @TypeConverter
    public static PartOfDay toPartOfDay(String partName) {
        return partName == null ? null : PartOfDay.valueOf(partName);
    }
}