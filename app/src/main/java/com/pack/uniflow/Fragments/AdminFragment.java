package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.DummyData.DummyStudent;
import com.pack.uniflow.DummyData.DummyUniversity;
import com.pack.uniflow.R;

import java.util.Arrays;
import java.util.List;

public class AdminFragment extends Fragment {

    private RecyclerView universitiesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        universitiesRecyclerView = view.findViewById(R.id.universitiesRecyclerView);
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy student lists
        List<DummyStudent> harvardStudents = Arrays.asList(
                new DummyStudent("Alice Johnson", "Online"),
                new DummyStudent("Bob Stone", "Offline"),
                new DummyStudent("Claire Hill", "Online")
        );

        List<DummyStudent> stanfordStudents = Arrays.asList(
                new DummyStudent("Dan Rogers", "Offline"),
                new DummyStudent("Eva Green", "Online")
        );

        // Dummy universities
        List<DummyUniversity> universityList = Arrays.asList(
                new DummyUniversity("Harvard University", harvardStudents),
                new DummyUniversity("Stanford University", stanfordStudents)
        );

        UniversityAdapter adapter = new UniversityAdapter(universityList);
        universitiesRecyclerView.setAdapter(adapter);

        return view;
    }
}
