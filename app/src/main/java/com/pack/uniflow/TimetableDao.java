package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao
public interface TimetableDao {
    @Insert
    void insert(Timetable Timetable);

    @Query("SELECT * FROM Timetable")
    List<Timetable> getAllTimetables();
}
