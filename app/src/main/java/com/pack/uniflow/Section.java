package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
@Entity(
        tableName = "section",
        foreignKeys = @ForeignKey(
                entity = Uni.class,
                parentColumns = "id",
                childColumns = "uni_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"name", "group_name", "uni_id"}, unique = true)}
)
public class Section {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "group_name")
    public String groupName;

    @ColumnInfo(name = "uni_id", index = true)
    public int uniId;

    @ColumnInfo(name = "academic_year")
    public String academicYear;
}