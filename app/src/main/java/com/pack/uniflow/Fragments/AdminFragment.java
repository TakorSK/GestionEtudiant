package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.pack.uniflow.Adapters.StudentAdapter;
import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.Models.Club;
import com.pack.uniflow.R;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private RecyclerView resultsRecyclerView;
    private LinearLayout resultsSection;
    private TextView noResultsText;
    private EditText searchEditText;

    private StudentAdapter studentAdapter;
    private UniversityAdapter universityAdapter;
    private ClubAdapter clubAdapter;

    private final List<Student> studentList = new ArrayList<>();
    private final List<Uni> universityList = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference universitiesRef = database.getReference("universities");
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference clubsRef = database.getReference("clubs");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // === UI Init ===
        resultsRecyclerView = rootView.findViewById(R.id.resultsRecyclerView);
        resultsSection = rootView.findViewById(R.id.resultsSection);
        noResultsText = rootView.findViewById(R.id.noResultsText);
        searchEditText = rootView.findViewById(R.id.searchEditText);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // === Initialize adapters empty first ===
        studentAdapter = new StudentAdapter(new ArrayList<>(), getContext());
        universityAdapter = new UniversityAdapter(new ArrayList<>(), getContext());
        clubAdapter = new ClubAdapter(new ArrayList<>(), getContext());

        // === Firebase Data Load ===
        loadUniversitiesFromFirebase();
        loadStudentsFromFirebase();
        loadClubsFromFirebase();

        // === Search Logic ===
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // === Add Button Functionalities ===
        Button btnAddUniversity = rootView.findViewById(R.id.btnAddUniversity);
        btnAddUniversity.setOnClickListener(v -> openAddUniversityFragment());

        Button btnAddClub = rootView.findViewById(R.id.btnAddClub);
        btnAddClub.setOnClickListener(v -> openAddClubFragment());

        Button btnAddStudent = rootView.findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(v -> openAddStudentFragment());

        return rootView;
    }

    // === Firebase Data Fetch ===
    private void loadUniversitiesFromFirebase() {
        universitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                universityList.clear();
                for (DataSnapshot uniSnap : snapshot.getChildren()) {
                    Uni uni = uniSnap.getValue(Uni.class);
                    if (uni != null) {
                        try {
                            uni.setId(Integer.parseInt(uniSnap.getKey()));
                        } catch (NumberFormatException ignored) {}
                        universityList.add(uni);
                    }
                }
                performSearch(searchEditText.getText().toString()); // Re-filter after loading
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load universities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentsFromFirebase() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    Student student = studentSnap.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                studentAdapter.updateList(studentList);

                String currentQuery = searchEditText.getText().toString();
                if (currentQuery.startsWith("stu:")) {
                    filterStudentList(currentQuery.substring(4).trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClubsFromFirebase() {
        clubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clubList.clear();
                for (DataSnapshot clubSnap : snapshot.getChildren()) {
                    Club club = clubSnap.getValue(Club.class);
                    if (club != null) {
                        clubList.add(club);
                    }
                }

                String currentQuery = searchEditText.getText().toString();
                if (currentQuery.startsWith("club:")) {
                    filterClubList(currentQuery.substring(5).trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load clubs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // === Search Logic ===
    private void performSearch(String query) {
        resultsSection.setVisibility(View.VISIBLE);

        if (query.startsWith("uni:")) {
            filterUniversityList(query.substring(4).trim());
        } else if (query.startsWith("stu:")) {
            filterStudentList(query.substring(4).trim());
        } else if (query.startsWith("club:")) {
            filterClubList(query.substring(5).trim());
        } else {
            filterUniversityList(query); // Default to uni
        }
    }

    private void filterUniversityList(String query) {
        List<Uni> filteredList = new ArrayList<>();
        for (Uni uni : universityList) {
            if (uni.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(uni);
            }
        }

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);

        universityAdapter = new UniversityAdapter(filteredList, getContext());
        resultsRecyclerView.setAdapter(universityAdapter);
    }

    private void filterStudentList(String query) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(student);
            }
        }

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);

        studentAdapter = new StudentAdapter(filteredList, getContext());
        resultsRecyclerView.setAdapter(studentAdapter);
    }

    private void filterClubList(String query) {
        List<Club> filteredList = new ArrayList<>();
        for (Club club : clubList) {
            if (club.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(club);
            }
        }

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);

        clubAdapter = new ClubAdapter(filteredList, getContext());
        resultsRecyclerView.setAdapter(clubAdapter);
    }

    // === Button Dialogs ===
    private void openAddUniversityFragment() {
        AddUniFragment fragment = new AddUniFragment();
        fragment.show(getParentFragmentManager(), "AddUniFragment");
    }

    private void openAddClubFragment() {
        AddClubFragment fragment = new AddClubFragment();
        fragment.show(getParentFragmentManager(), "AddClubFragment");
    }

    private void openAddStudentFragment() {
        AddStudentFragment fragment = new AddStudentFragment();
        fragment.show(getParentFragmentManager(), "AddStudentFragment");
    }
}
