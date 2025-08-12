package com.pack.uniflow;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Student {
    private String id;
    private String sectionId;
    @NonNull private String email;
    @NonNull private String fullName;
    private int age;
    private String telephone;
    private String uniId;
    private String clubId;
    private boolean isOnline = false;
    private boolean isAdmin = false;
    private String registrationDate;
    private String lastLogin;
    @NonNull private String password;
    private String bio;
    private String profilePictureUri;
    private List<String> tags; // 🔹 NEW

    // Required empty constructor for Firebase
    public Student() {
        this.tags = new ArrayList<>();
    }

    public Student(@NonNull String email, @NonNull String fullName, int age,
                   @NonNull String telephone, @NonNull String uniId,
                   @NonNull String password) {
        this.email = email;
        this.fullName = fullName;
        this.age = age;
        this.telephone = telephone;
        this.uniId = uniId;
        this.password = password;
        this.registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        this.profilePictureUri = "";
        this.isAdmin = false;
        this.tags = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    @NonNull public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull public String getFullName() { return fullName; }
    public void setFullName(@NonNull String fullName) { this.fullName = fullName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getUniId() { return uniId; }
    public void setUniId(String uniId) { this.uniId = uniId; }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    @NonNull public String getPassword() { return password; }
    public void setPassword(@NonNull String password) { this.password = password; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePictureUri() { return profilePictureUri; }
    public void setProfilePictureUri(String profilePictureUri) { this.profilePictureUri = profilePictureUri; }

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
