package com.pack.uniflow.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "posts") // Ensure table name is correct
public class Post {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") // Maps field 'id' to column 'id'
    private int id;

    @NonNull
    @ColumnInfo(name = "title") // Maps field 'title' to column 'title'
    private String title;

    @ColumnInfo(name = "description") // Maps field 'description' to column 'description'
    private String description;

    @ColumnInfo(name = "image_uri") // Maps field 'imageUri' to column 'image_uri'
    private String imageUri;

    // Use camelCase field name, map to snake_case column name
    @ColumnInfo(name = "author_id") // Maps field 'authorId' to column 'author_id'
    private int authorId;

    // Use camelCase field name, map to snake_case column name
    // Sticking with String based on your constructor
    @ColumnInfo(name = "created_at") // Maps field 'createdAt' to column 'created_at'
    private String createdAt;

    // --- Removed Duplicate/Unused Fields ---
    // public double content; // Removed - Unused?
    // public int author_id; // Removed - Duplicate
    // public long created_at; // Removed - Duplicate and type mismatch

    // Constructors, getters and setters
    public Post() {} // Keep default constructor for Room

    // Constructor using the correct fields
    public Post(@NonNull String title, String description, String imageUri, int authorId) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.authorId = authorId; // Assign to the correct field
        // Assign to the correct field
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // Add all getters and setters for the private fields (Room requires them)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public int getAuthorId() { return authorId; } // Getter for authorId
    public void setAuthorId(int authorId) { this.authorId = authorId; } // Setter for authorId

    public String getCreatedAt() { return createdAt; } // Getter for createdAt
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; } // Setter for createdAt
}