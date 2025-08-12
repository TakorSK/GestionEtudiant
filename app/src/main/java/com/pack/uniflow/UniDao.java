package com.pack.uniflow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UniDao {

    private final DatabaseReference unisRef;

    public UniDao() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        unisRef = FirebaseDatabase.getInstance().getReference("universities");
    }

    // Insert
    public void insert(Uni uni, InsertCallback callback) {
        String uniId = unisRef.push().getKey();
        uni.setId(0); // You might want to change int -> String in Uni model for consistency
        unisRef.child(uniId).setValue(uni)
                .addOnSuccessListener(aVoid -> callback.onInserted(uniId))
                .addOnFailureListener(callback::onError);
    }

    // Get all universities
    public void getAllUnis(LoadCallback callback) {
        unisRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Uni> unis = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Uni uni = snapshot.getValue(Uni.class);
                    unis.add(uni);
                }
                callback.onLoaded(unis);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Get unis by tag
    public void getUnisByTag(String tag, LoadCallback callback) {
        unisRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Uni> unis = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Uni uni = snapshot.getValue(Uni.class);
                    if (uni != null && uni.getTags() != null && uni.getTags().contains(tag)) {
                        unis.add(uni);
                    }
                }
                callback.onLoaded(unis);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface InsertCallback {
        void onInserted(String uniId);
        void onError(Exception e);
    }

    public interface LoadCallback {
        void onLoaded(List<Uni> unis);
        void onError(Exception e);
    }

    public interface SingleLoadCallback {
        void onLoaded(Uni uni);
        void onError(Exception e);
    }

    public interface PasswordCallback {
        void onLoaded(String password);
        void onError(Exception e);
    }

    public interface IdCallback {
        void onLoaded(String uniId);
        void onError(Exception e);
    }
}
