package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao
public interface StudentDao {
    @Insert
    void insert(Student Student);

    @Query("SELECT * FROM Student")
    List<Student> getAllStudents();
}
