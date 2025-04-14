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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClubsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClubsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClubsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubsFragment newInstance(String param1, String param2) {
        ClubsFragment fragment = new ClubsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.clubsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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