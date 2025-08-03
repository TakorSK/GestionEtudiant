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

    // Equivalent to @Insert
    public void insert(Uni uni, InsertCallback callback) {
        int uniId = Integer.parseInt(unisRef.push().getKey());
        uni.setId(uniId);
        unisRef.child(String.valueOf(uniId)).setValue(uni)
                .addOnSuccessListener(aVoid -> callback.onInserted(String.valueOf(uniId)))
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllUnis()
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

    // Equivalent to findByName()
    public void findByName(String name, SingleLoadCallback callback) {
        unisRef.orderByChild("name").equalTo(name).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Uni uni = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                uni = snapshot.getValue(Uni.class);
                            }
                        }
                        callback.onLoaded(uni);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Equivalent to getById()
    public void getById(String uniId, SingleLoadCallback callback) {
        unisRef.child(uniId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Uni uni = dataSnapshot.getValue(Uni.class);
                        callback.onLoaded(uni);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Equivalent to getUniPasswordById()
    public void getUniPasswordById(String uniId, PasswordCallback callback) {
        unisRef.child(uniId).child("uniPassword")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String password = dataSnapshot.getValue(String.class);
                        callback.onLoaded(password);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Equivalent to getIdByName()
    public void getIdByName(String name, IdCallback callback) {
        unisRef.orderByChild("name").equalTo(name).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uniId = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                uniId = snapshot.getKey();
                            }
                        }
                        callback.onLoaded(uniId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    // Callback interfaces
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