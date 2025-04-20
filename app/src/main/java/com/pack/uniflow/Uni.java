package com.pack.uniflow;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "uni")
public class Uni {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "established_year")
    public int establishedYear;

    @ColumnInfo(name = "website")
    public String website;

    @ColumnInfo(name = "associated_student_ids")
    public String associatedStudentIds; // e.g., "1,2,5,10"

    @ColumnInfo(name = "UniPassword")
    public String UniPassword;
    // ---------------------
    // Helper Methods
    // ---------------------

    public List<Integer> getAssociatedStudentIdList() {
        if (associatedStudentIds == null || associatedStudentIds.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> list = new ArrayList<>();
        for (String s : associatedStudentIds.split(",")) {
            try {
                list.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return list;
    }

    public void setAssociatedStudentIdList(List<Integer> ids) {
        this.associatedStudentIds = TextUtils.join(",", ids);
    }
}
