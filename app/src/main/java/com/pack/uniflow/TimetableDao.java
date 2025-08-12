package com.pack.uniflow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class TimetableDao {

    private final DatabaseReference timetablesRef;

    public TimetableDao() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        timetablesRef = FirebaseDatabase.getInstance().getReference("timetables");
    }

    // Equivalent to @Insert
    public void insert(Timetable timetable, InsertCallback callback) {
        String timetableId = timetablesRef.push().getKey();
        timetable.setId(timetableId);
        timetablesRef.child(timetableId).setValue(timetable)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllTimetables()
    public void getAllTimetables(LoadCallback callback) {
        timetablesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Timetable> timetables = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable timetable = snapshot.getValue(Timetable.class);
                    timetables.add(timetable);
                }
                callback.onLoaded(timetables);
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
        void onLoaded(List<Timetable> timetables);
        void onError(Exception e);
    }
}