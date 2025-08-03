package com.pack.uniflow;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UniflowDB {
    private static UniflowDB instance;
    private final DatabaseReference databaseRef;

    // Database paths
    public final DatabaseReference universitiesRef;
    public final DatabaseReference clubsRef;
    public final DatabaseReference sectionsRef;
    public final DatabaseReference studentsRef;
    public final DatabaseReference timetablesRef;
    public final DatabaseReference postsRef;

    private UniflowDB() {
        // Enable disk persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize all references
        universitiesRef = databaseRef.child("universities");
        clubsRef = databaseRef.child("clubs");
        sectionsRef = databaseRef.child("sections");
        studentsRef = databaseRef.child("students");
        timetablesRef = databaseRef.child("timetables");
        postsRef = databaseRef.child("posts");
    }

    public static synchronized UniflowDB getInstance() {
        if (instance == null) {
            instance = new UniflowDB();
        }
        return instance;
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }
}