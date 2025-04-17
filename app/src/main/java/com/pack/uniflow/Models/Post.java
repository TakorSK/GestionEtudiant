package com.pack.uniflow.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "posts")
public class Post {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_uri")
    private String imageUri; // Store URI/path to actual image file

    @ColumnInfo(name = "author_id")
    private int authorId;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    // Constructors, getters and setters
    public Post() {}

    public Post(@NonNull String title, String description, String imageUri, int authorId) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.authorId = authorId;
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // Add all getters and setters (Room requires them)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}