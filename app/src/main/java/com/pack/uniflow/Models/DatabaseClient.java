package com.pack.uniflow.Models;

import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class DatabaseClient {
    private static DatabaseClient instance;
    private DatabaseReference databaseRef;
    private static final Object LOCK = new Object();

    private DatabaseClient(Context context) {
        // Enable disk persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Insert default data
        insertDefaultData();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DatabaseClient(context);
                }
            }
        }
        return instance;
    }

    public DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    private void insertDefaultData() {
        // Create universities
        String nationalUniId = databaseRef.child("universities").push().getKey();
        String techUniId = databaseRef.child("universities").push().getKey();

        Map<String, Object> nationalUni = new HashMap<>();
        nationalUni.put("name", "National University");
        nationalUni.put("location", "Main Campus");
        nationalUni.put("establishedYear", 1950);
        nationalUni.put("website", "www.nationaluni.edu");

        Map<String, Object> techUni = new HashMap<>();
        techUni.put("name", "Tech Institute");
        techUni.put("location", "Tech Park");
        techUni.put("establishedYear", 1995);
        techUni.put("website", "www.techinstitute.edu");

        // Write universities
        databaseRef.child("universities").child(nationalUniId).setValue(nationalUni)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FB_INSERT", "Inserted National University");
                    // Insert clubs for this university
                    insertClub("Chess Club", "For chess enthusiasts", nationalUniId);
                    insertClub("Debate Society", "Public speaking", nationalUniId);
                });

        databaseRef.child("universities").child(techUniId).setValue(techUni)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FB_INSERT", "Inserted Tech Institute");
                    // Insert clubs for this university
                    insertClub("Coding Club", "Learn programming", techUniId);
                    insertClub("Robotics Team", "Build robots", techUniId);
                });
    }

    private void insertClub(String name, String description, String uniId) {
        String clubId = databaseRef.child("clubs").push().getKey();

        Map<String, Object> club = new HashMap<>();
        club.put("name", name);
        club.put("description", description);
        club.put("uniId", uniId);

        databaseRef.child("clubs").child(clubId).setValue(club)
                .addOnSuccessListener(aVoid ->
                        Log.d("FB_INSERT", "Inserted Club: " + name))
                .addOnFailureListener(e ->
                        Log.e("FB_INIT", "Failed to insert club: " + name, e));
    }
}