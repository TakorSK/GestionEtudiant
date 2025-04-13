package com.pack.uniflow;
import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {Uni.class, Club.class, Section.class, Student.class, Timetable.class}, version = 4, exportSchema = false)

public abstract class UniflowDB extends RoomDatabase {
    public abstract UniDao uniDao();
    public abstract ClubDao clubDao();
    public abstract SectionDao sectionDao();
    public abstract StudentDao studentDao();
    public abstract TimetableDao timetableDao();
}
