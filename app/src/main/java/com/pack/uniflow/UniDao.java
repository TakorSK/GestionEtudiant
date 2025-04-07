package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UniDao {

    @Insert
    void insert(Uni uni);

    @Query("SELECT * FROM Uni")
    List<Uni> getAllUnis();
}