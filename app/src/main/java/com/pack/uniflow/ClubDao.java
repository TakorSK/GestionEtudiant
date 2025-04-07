package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ClubDao {

    @Insert
    void insert(Club club);

    @Query("SELECT * FROM Club")
    List<Club> getAllClubs();
}
