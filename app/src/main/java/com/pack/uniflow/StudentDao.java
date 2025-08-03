package com.pack.uniflow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    private final DatabaseReference studentsRef;

    public StudentDao() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        studentsRef = FirebaseDatabase.getInstance().getReference("students");
    }

    // Equivalent to @Insert
    public void insert(Student student, InsertCallback callback) {
        String studentId = studentsRef.push().getKey();
        student.setId(studentId);
        studentsRef.child(studentId).setValue(student)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllStudents()
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

    // Equivalent to findByEmail()
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

    // Equivalent to getLatestStudent()
    public void getLatestStudent(SingleLoadCallback callback) {
        studentsRef.orderByKey().limitToLast(1)
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

    // Equivalent to getStudentById()
    public void getStudentById(String studentId, SingleLoadCallback callback) {
        studentsRef.child(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        callback.onLoaded(student);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Equivalent to setAllOffline()
    public void setAllOffline(CompletionCallback callback) {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    studentIds.add(snapshot.getKey());
                }

                DatabaseReference.CompletionListener completionListener =
                        new DatabaseReference.CompletionListener() {
                            int completed = 0;
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                completed++;
                                if (completed == studentIds.size()) {
                                    callback.onSuccess();
                                }
                            }
                        };

                for (String id : studentIds) {
                    studentsRef.child(id).child("isOnline").setValue(false, completionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Equivalent to @Update
    public void update(Student student, CompletionCallback callback) {
        studentsRef.child(student.getId()).setValue(student)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getOnlineStudent()
    public void getOnlineStudent(SingleLoadCallback callback) {
        studentsRef.orderByChild("isOnline").equalTo(true)
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

    // Equivalent to getStudentsByUniId()
    public void getStudentsByUniId(String uniId, LoadCallback callback) {
        studentsRef.orderByChild("uniId").equalTo(uniId)
                .addValueEventListener(new ValueEventListener() {
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

    // Callback interfaces
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