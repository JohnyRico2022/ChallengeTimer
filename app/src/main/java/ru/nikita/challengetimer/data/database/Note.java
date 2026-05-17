package ru.nikita.challengetimer.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;

import ru.nikita.challengetimer.common.PartOfDay;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public int state;
    public int wish;
    public String description;
    public long date;
    public DayOfWeek day;
    public PartOfDay partOfDay;
    public int marathonDays; // к какому марафону относится заметка

    public Note() {
    }

    public Note(String title, int state, int wish, String description, long date,
                DayOfWeek day, PartOfDay partOfDay, int marathonDays) {
        this.title = title;
        this.state = state;
        this.wish = wish;
        this.description = description;
        this.date = date;
        this.day = day;
        this.partOfDay = partOfDay;
        this.marathonDays = marathonDays;
    }
}