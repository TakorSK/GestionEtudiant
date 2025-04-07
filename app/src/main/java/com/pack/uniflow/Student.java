package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
@Entity(
        tableName = "student",
        foreignKeys = {
                @ForeignKey(entity = Section.class, parentColumns = "id", childColumns = "section_id", onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Uni.class, parentColumns = "id", childColumns = "uni_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Club.class, parentColumns = "id", childColumns = "club_id", onDelete = ForeignKey.SET_NULL)
        }
)
public class Student {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "section_id", index = true)
    public Integer sectionId; // Nullable (because SET NULL on delete)

    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @NonNull
    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "uni_id", index = true)
    public int uniId;

    @ColumnInfo(name = "club_id", index = true)
    public Integer clubId; // Nullable

    @ColumnInfo(name = "is_online")
    public boolean isOnline = false;

    @ColumnInfo(name = "registration_date")
    public String registrationDate; // Store as String ("YYYY-MM-DD")

    @ColumnInfo(name = "last_login")
    public String lastLogin; // Or use Date and a TypeConverter
}
