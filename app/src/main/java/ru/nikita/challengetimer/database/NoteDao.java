package ru.nikita.challengetimer.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Note> notes);

    @Query("SELECT * FROM notes ORDER BY date DESC")
    List<Note> getAllNotes(); // Пока простой синхронный запрос
}
