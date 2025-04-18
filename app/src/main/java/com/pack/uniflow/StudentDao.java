
package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
    @Query("UPDATE student SET is_online = 0")
    void setAllOffline();
    @Update
    void update(Student student);
    @Query("SELECT * FROM Student WHERE is_online = 1 LIMIT 1")
    Student getOnlineStudent();
}
