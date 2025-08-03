package com.pack.uniflow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClubDao {

    private final DatabaseReference clubsRef;

    public ClubDao() {
        clubsRef = FirebaseDatabase.getInstance().getReference("clubs");
    }

    // Equivalent to @Insert
    public void insert(Club club, InsertCallback callback) {
        String clubId = clubsRef.push().getKey();
        club.setId(clubId);
        clubsRef.child(clubId).setValue(club)
                .addOnSuccessListener(aVoid -> callback.onInserted(clubId))
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllClubs()
    public void getAllClubs(LoadCallback callback) {
        clubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Club> clubs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Club club = snapshot.getValue(Club.class);
                    clubs.add(club);
                }
                callback.onLoaded(clubs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface InsertCallback {
        void onInserted(String clubId);
        void onError(Exception e);
    }

    public interface LoadCallback {
        void onLoaded(List<Club> clubs);
        void onError(Exception e);
    }
}