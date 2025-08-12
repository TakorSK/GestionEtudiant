package com.pack.uniflow.Models;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class Uni {
    private int id;
    @NonNull private String name;
    private String location;
    private int establishedYear;
    private String website;
    private List<String> associatedStudentIds;
    private String uniPassword;
    private List<String> tags; // 🔹 NEW

    // Required empty constructor for Firebase
    public Uni() {
        this.associatedStudentIds = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Uni(@NonNull String name, String location, int establishedYear,
               String website, String uniPassword) {
        this.name = name;
        this.location = location;
        this.establishedYear = establishedYear;
        this.website = website;
        this.uniPassword = uniPassword;
        this.associatedStudentIds = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(int establishedYear) { this.establishedYear = establishedYear; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public List<String> getAssociatedStudentIds() { return associatedStudentIds; }
    public void setAssociatedStudentIds(List<String> associatedStudentIds) { this.associatedStudentIds = associatedStudentIds; }

    public String getUniPassword() { return uniPassword; }
    public void setUniPassword(String uniPassword) { this.uniPassword = uniPassword; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    // --- Tag helpers ---
    public void addTag(String tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    // --- Student association helpers ---
    public boolean containsStudentId(String studentId) {
        return associatedStudentIds.contains(studentId);
    }

    public void addStudentId(String studentId) {
        if (!associatedStudentIds.contains(studentId)) {
            associatedStudentIds.add(studentId);
        }
    }

    public void removeStudentId(String studentId) {
        associatedStudentIds.remove(studentId);
    }
}
