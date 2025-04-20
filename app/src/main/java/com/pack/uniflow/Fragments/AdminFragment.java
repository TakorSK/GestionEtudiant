package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminFragment extends Fragment {

    private RecyclerView universitiesRecyclerView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        universitiesRecyclerView = view.findViewById(R.id.universitiesRecyclerView);
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnAddUniversity = view.findViewById(R.id.btnAddUniversity);
        btnAddUniversity.setOnClickListener(v -> openAddUniversityFragment());

        // New Button for Adding Club
        Button btnAddClub = view.findViewById(R.id.btnAddClub);  // Assuming you have this button in the XML
        btnAddClub.setOnClickListener(v -> openAddClubFragment());

        loadDataFromDatabase();
        return view;
    }

    private void openAddUniversityFragment() {
        AddUniFragment addUniFragment = new AddUniFragment();
        addUniFragment.show(getParentFragmentManager(), "AddUniFragment");
    }

    // Method to open AddClubFragment
    private void openAddClubFragment() {
        AddClubFragment addClubFragment = new AddClubFragment();
        addClubFragment.show(getParentFragmentManager(), "AddClubFragment");
    }

    private void loadDataFromDatabase() {
        executorService.execute(() -> {
            try {
                List<Uni> universities = DatabaseClient.getInstance(getContext())
                        .getDatabase()
                        .uniDao()
                        .getAll();

                List<UniversityWithStudents> universityList = new ArrayList<>();
                for (Uni uni : universities) {
                    List<Student> students = DatabaseClient.getInstance(getContext())
                            .getDatabase()
                            .studentDao()
                            .getStudentsByUniId(uni.id);
                    universityList.add(new UniversityWithStudents(uni, students));
                }

                requireActivity().runOnUiThread(() -> {
                    UniversityAdapter adapter = new UniversityAdapter(universityList, getContext());
                    universitiesRecyclerView.setAdapter(adapter);
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        });
    }

    public static class UniversityWithStudents {
        public final Uni university;
        public final List<Student> students;

        public UniversityWithStudents(Uni university, List<Student> students) {
            this.university = university;
            this.students = students;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
