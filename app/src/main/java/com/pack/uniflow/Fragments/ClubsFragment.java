package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Adapters.ClubAdapter;
import com.pack.uniflow.Models.Club;
import com.pack.uniflow.R;

import java.util.ArrayList;
import java.util.List;

public class ClubsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClubAdapter adapter;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference clubsRef = database.getReference("clubs");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        recyclerView = view.findViewById(R.id.clubsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ Fix: Provide both arguments as required
        adapter = new ClubAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        loadClubsFromFirebase();
        return view;
    }

    private void loadClubsFromFirebase() {
        clubsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Club> clubs = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Club club = snap.getValue(Club.class);
                    if (club != null) clubs.add(club);
                }


                adapter.updateClubs(clubs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load clubs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
