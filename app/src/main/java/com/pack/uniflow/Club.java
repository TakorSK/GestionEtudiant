package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
@Entity(
        tableName = "club",
        foreignKeys = @ForeignKey(
                entity = Uni.class,
                parentColumns = "id",
                childColumns = "uni_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class Club {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "uni_id", index = true)
    public int uniId;
}