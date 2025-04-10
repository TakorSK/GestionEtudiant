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
    @Query("SELECT * FROM student WHERE email = :email LIMIT 1")
    Student findByEmail(String email);
    @Query("SELECT * FROM student ORDER BY id DESC LIMIT 1")
    Student getLatestStudent();
}
