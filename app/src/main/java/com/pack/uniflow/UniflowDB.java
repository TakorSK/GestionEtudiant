package com.pack.uniflow;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pack.uniflow.Models.Post;

@Database(entities = {Uni.class, Club.class, Section.class, Student.class, Timetable.class, Post.class}, version = 8, exportSchema = false)

public abstract class UniflowDB extends RoomDatabase {
    public abstract UniDao uniDao();
    public abstract ClubDao clubDao();
    public abstract SectionDao sectionDao();
    public abstract StudentDao studentDao();
    public abstract TimetableDao timetableDao();
    public abstract PostDao postDao();
}
