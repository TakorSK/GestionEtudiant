package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "uni")
public class Uni {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "established_year")
    public int establishedYear; // Stored as int (YEAR in MySQL = 4 digits)

    @ColumnInfo(name = "website")
    public String website;
}
