package com.pack.uniflow;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(
        tableName = "student",
        foreignKeys = {
                @ForeignKey(entity = Section.class,
                        parentColumns = "id",
                        childColumns = "section_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Uni.class,
                        parentColumns = "id",
                        childColumns = "uni_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Club.class,
                        parentColumns = "id",
                        childColumns = "club_id",
                        onDelete = ForeignKey.SET_NULL)
        }
)
public class Student {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "section_id", index = true)
    public Integer sectionId;

    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @NonNull
    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "age")
    public int age;

    @ColumnInfo(name = "telephone")
    public String telephone;

    @ColumnInfo(name = "uni_id", index = true)
    public int uniId;

    @ColumnInfo(name = "club_id", index = true)
    public Integer clubId;

    @ColumnInfo(name = "is_online")
    public boolean isOnline = false;

    @ColumnInfo(name = "registration_date")
    public String registrationDate;

    @ColumnInfo(name = "last_login")
    public String lastLogin;

    @NonNull
    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "Bio")
    public String Bio;

    @ColumnInfo(name = "profile_picture_uri")
    public String profilePictureUri;

    public Student(int id, @NonNull String email, @NonNull String fullName,
                   int age, @NonNull String telephone, int uniId,
                   @NonNull String password) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.age = age;
        this.telephone = telephone;
        this.uniId = uniId;
        this.password = password;
        this.registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        this.profilePictureUri = "";
    }
}