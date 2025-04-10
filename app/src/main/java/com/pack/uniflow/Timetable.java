package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
@Entity(
        tableName = "timetable",
        foreignKeys = @ForeignKey(
                entity = Section.class,
                parentColumns = "id",
                childColumns = "section_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class Timetable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "section_id", index = true)
    public int sectionId;

}
