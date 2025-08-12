package com.pack.uniflow;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class Club {
    private String id;
    @NonNull private String name;
    private String description;
    private String uniId;
    private String uniName;
    private List<String> tags; // 🔹 NEW

    // Required empty constructor for Firebase
    public Club() {
        this.tags = new ArrayList<>();
    }

    public Club(@NonNull String name, String description, String uniId, String uniName) {
        this.name = name;
        this.description = description;
        this.uniId = uniId;
        this.uniName = uniName;
        this.tags = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUniId() { return uniId; }
    public void setUniId(String uniId) { this.uniId = uniId; }

    public String getUniName() { return uniName; }
    public void setUniName(String uniName) { this.uniName = uniName; }

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
}
