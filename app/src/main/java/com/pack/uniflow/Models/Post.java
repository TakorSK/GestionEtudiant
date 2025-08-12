package com.pack.uniflow.Models;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Post {
    private String id; // Changed from int to String for Firebase
    private String title;
    private String description;
    private String imageUri;
    private String authorId; // Changed from int to String (Firebase typically uses String IDs)
    private String createdAt;
    private String authorName; // Recommended for denormalization
    private String authorProfileImage; // Recommended for denormalization
    private List<String> tags; // Added tags field

    // Required empty constructor for Firebase
    public Post() {
    }

    // Constructor for creating new posts
    public Post(@NonNull String title, String description, String imageUri, String authorId,
                String authorName, String authorProfileImage, List<String> tags) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorProfileImage = authorProfileImage;
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        this.tags = tags;
    }

    // Getters and setters (required for Firebase serialization)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfileImage() {
        return authorProfileImage;
    }

    public void setAuthorProfileImage(String authorProfileImage) {
        this.authorProfileImage = authorProfileImage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
