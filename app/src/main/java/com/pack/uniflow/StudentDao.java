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
    @Query("SELECT * FROM Student WHERE email = :email LIMIT 1")
    Student findByEmail(String email);
    @Query("SELECT * FROM Student ORDER BY id DESC LIMIT 1")
    Student getLatestStudent();
    @Query("SELECT * FROM Student WHERE id = :id LIMIT 1")
    Student getStudentById(int id);
}
