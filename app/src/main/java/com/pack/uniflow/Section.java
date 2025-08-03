package com.pack.uniflow;

import androidx.annotation.NonNull;

import java.util.List;

public class Section {
    private String id; // Changed from int to String for Firebase
    @NonNull
    private String name;
    @NonNull
    private String groupName;
    private String uniId; // Changed from int to String
    private String academicYear;
    private List<String> associatedStudentIds; // Changed from String to List<String>

    // Required empty constructor for Firebase
    public Section() {
    }

    public Section(@NonNull String name, @NonNull String groupName, String uniId,
                   String academicYear, List<String> associatedStudentIds) {
        this.name = name;
        this.groupName = groupName;
        this.uniId = uniId;
        this.academicYear = academicYear;
        this.associatedStudentIds = associatedStudentIds;
    }

    // Getters and setters
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

    @NonNull
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(@NonNull String groupName) {
        this.groupName = groupName;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public List<String> getAssociatedStudentIds() {
        return associatedStudentIds;
    }

    public void setAssociatedStudentIds(List<String> associatedStudentIds) {
        this.associatedStudentIds = associatedStudentIds;
    }
}