package com.pack.uniflow.Models;

public class Post {
    private String title;
    private String description;
    private int imageResId; // 0 means no image

    public Post(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
