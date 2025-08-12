package com.pack.uniflow.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SectionDao {

    private final DatabaseReference sectionsRef;

    public SectionDao() {
        sectionsRef = FirebaseDatabase.getInstance().getReference("sections");
    }

    // Equivalent to @Insert
    public void insert(Section section, InsertCallback callback) {
        String sectionId = sectionsRef.push().getKey();
        section.setId(sectionId);
        sectionsRef.child(sectionId).setValue(section)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // Equivalent to getAllSections()
    public void getAllSections(LoadCallback callback) {
        sectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Section> sections = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Section section = snapshot.getValue(Section.class);
                    sections.add(section);
                }
                callback.onLoaded(sections);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Equivalent to getById()
    public void getById(String sectionId, SingleLoadCallback callback) {
        sectionsRef.child(sectionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Section section = dataSnapshot.getValue(Section.class);
                callback.onLoaded(section);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface InsertCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface LoadCallback {
        void onLoaded(List<Section> sections);
        void onError(Exception e);
    }

    public interface SingleLoadCallback {
        void onLoaded(Section section);
        void onError(Exception e);
    }
}