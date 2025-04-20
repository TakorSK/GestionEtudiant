package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UniDao {

    @Insert
    long insert(Uni uni);

    @Query("SELECT * FROM Uni")
    List<Uni> getAllUnis();
    @Query("SELECT * FROM uni WHERE name = :name LIMIT 1")
    Uni findByName(String name);
    @Query("SELECT * FROM uni WHERE id = :id")
    Uni getById(int id);
    @Query("SELECT * FROM uni")
    List<Uni> getAll();

    @Query("SELECT uni_password FROM uni WHERE id = :id")
    String getUniPasswordById(int id);
    @Query("Select * from uni where id = :id")
    Uni getUniversityById(int id);
    @Query("SELECT id FROM uni WHERE name = :name LIMIT 1")
    int getIdByName(String name);
}
