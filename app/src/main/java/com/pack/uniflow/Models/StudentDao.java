package com.pack.uniflow.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    private final DatabaseReference studentsRef;

    public StudentDao() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        studentsRef = FirebaseDatabase.getInstance().getReference("students");
    }

    // Insert
    public void insert(Student student, InsertCallback callback) {
        String studentId = studentsRef.push().getKey();
        student.setId(studentId);
        studentsRef.child(studentId).setValue(student)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Get all students
    public void getAllStudents(LoadCallback callback) {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    students.add(student);
                }
                callback.onLoaded(students);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Get students by tag
    public void getStudentsByTag(String tag, LoadCallback callback) {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    if (student != null && student.getTags() != null && student.getTags().contains(tag)) {
                        students.add(student);
                    }
                }
                callback.onLoaded(students);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Find by email
    public void findByEmail(String email, SingleLoadCallback callback) {
        studentsRef.orderByChild("email").equalTo(email)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                student = snapshot.getValue(Student.class);
                            }
                        }
                        callback.onLoaded(student);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Other methods remain the same...
    // (getLatestStudent, getStudentById, setAllOffline, update, getOnlineStudent, getStudentsByUniId)

    public interface InsertCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface LoadCallback {
        void onLoaded(List<Student> students);
        void onError(Exception e);
    }

    public interface SingleLoadCallback {
        void onLoaded(Student student);
        void onError(Exception e);
    }

    public interface CompletionCallback {
        void onSuccess();
        void onError(Exception e);
    }
}
