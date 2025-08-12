package com.pack.uniflow.Models;

import androidx.annotation.NonNull;

public class Club {
    private String id; // Changed from int to String for Firebase
    @NonNull
    private String name;
    private String description;
    private String uniId; // Changed from int to String
    private String uniName; // Denormalized data for easier access

    // Required empty constructor for Firebase
    public Club() {
    }

    public Club(@NonNull String name, String description, String uniId, String uniName) {
        this.name = name;
        this.description = description;
        this.uniId = uniId;
        this.uniName = uniName;
    }

    // Getters and setters (required for Firebase serialization)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }
}