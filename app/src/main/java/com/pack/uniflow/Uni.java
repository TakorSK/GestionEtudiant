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
    public String associatedStudentIds;

    @ColumnInfo(name = "uni_password")
    public String uniPassword;

    public List<Integer> getAssociatedStudentIdList() {
        List<Integer> ids = new ArrayList<>();
        if (!TextUtils.isEmpty(associatedStudentIds)) {
            for (String id : associatedStudentIds.split(",")) {
                try {
                    ids.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        return ids;
    }

    public boolean containsStudentId(int studentId) {
        return getAssociatedStudentIdList().contains(studentId);
    }

    public void addStudentId(int studentId) {
        List<Integer> ids = getAssociatedStudentIdList();
        if (!ids.contains(studentId)) {
            ids.add(studentId);
            associatedStudentIds = TextUtils.join(",", ids);
        }
    }
    public void setAssociatedStudentIdList(List<Integer> ids) {
        this.associatedStudentIds = TextUtils.join(",", ids);
    }

}
