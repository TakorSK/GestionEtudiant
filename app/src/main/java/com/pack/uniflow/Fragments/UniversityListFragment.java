package com.pack.uniflow.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.R;
import com.pack.uniflow.Models.Uni;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UniversityListFragment extends Fragment {

    private RecyclerView recyclerView;
    private UniversityAdapter adapter;
    private List<Uni> universityList;

    private DatabaseReference universityRef;

    public UniversityListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_university_list, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewUniversities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        universityList = new ArrayList<>();
        adapter = new UniversityAdapter(universityList, getContext());
        recyclerView.setAdapter(adapter);

        universityRef = FirebaseDatabase.getInstance().getReference("universities");

        loadUniversitiesFromFirebase();

        return rootView;
    }

    private void loadUniversitiesFromFirebase() {
        universityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                universityList.clear();
                for (DataSnapshot uniSnapshot : snapshot.getChildren()) {
                    Uni uni = uniSnapshot.getValue(Uni.class);
                    if (uni != null) {
                        universityList.add(uni);
                    }
                }
                adapter.updateList(universityList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load universities: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

