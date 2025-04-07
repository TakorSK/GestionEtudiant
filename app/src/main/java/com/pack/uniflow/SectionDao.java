package com.pack.uniflow;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SectionDao {

    @Insert
    void insert(Section Section);

    @Query("SELECT * FROM Section")
    List<Section> getAllSections();
}