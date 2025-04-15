package com.pack.uniflow.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pack.uniflow.Adapters.ClubAdapter;
import com.pack.uniflow.Club;
import com.pack.uniflow.R;

import java.util.ArrayList;
import java.util.List;



public class ClubsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.clubsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //if
        List<Club> dummyClubs = new ArrayList<>();

        Club chess = new Club();
        chess.name = "Chess Club";
        chess.description = "For chess lovers";
        chess.uniId = 1;

        Club coding = new Club();
        coding.name = "Coding Club";
        coding.description = "We write bugs professionally.";
        coding.uniId = 1;

        Club art = new Club();
        art.name = "Art Society";
        art.description = "Express yourself in color!";
        art.uniId = 1;

        dummyClubs.add(chess);
        dummyClubs.add(coding);
        dummyClubs.add(art);

        ClubAdapter adapter = new ClubAdapter(dummyClubs);
        recyclerView.setAdapter(adapter);

        return view;
    }

}