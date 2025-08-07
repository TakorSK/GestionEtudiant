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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFragment extends Fragment {

    private RecyclerView universitiesRecyclerView;
    private UniversityAdapter adapter;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference unisRef = database.getReference("universities");
    private final DatabaseReference studentsRef = database.getReference("students");

    private final List<Uni> universityListRaw = new ArrayList<>();
    private final Map<String, List<Student>> studentMap = new HashMap<>();

    private boolean universitiesLoaded = false;
    private boolean studentsLoaded = false;

    private ValueEventListener uniListener;
    private ValueEventListener studentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        universitiesRecyclerView = view.findViewById(R.id.universitiesRecyclerView);
        universitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UniversityAdapter(new ArrayList<UniversityWithStudents>(), getContext());
        universitiesRecyclerView.setAdapter(adapter);

        Button btnAddUniversity = view.findViewById(R.id.btnAddUniversity);
        btnAddUniversity.setOnClickListener(v -> openAddUniversityFragment());

        Button btnAddClub = view.findViewById(R.id.btnAddClub);
        btnAddClub.setOnClickListener(v -> openAddClubFragment());

        Button btnAddStudent = view.findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(v -> openAddStudentFragment());

        fetchData();
        return view;
    }

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

    private void fetchData() {
        loadStudents();
        loadUniversities();
    }

    private void loadStudents() {
        studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentMap.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Student student = snap.getValue(Student.class);
                    if (student == null) continue;
                    student.setId(snap.getKey());
                    String uniId = student.getUniId();
                    if (uniId == null) uniId = "unknown";

                    List<Student> list = studentMap.get(uniId);
                    if (list == null) {
                        list = new ArrayList<>();
                        studentMap.put(uniId, list);
                    }
                    list.add(student);
                }
                studentsLoaded = true;
                composeAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        };
        studentsRef.addValueEventListener(studentListener);
    }

    private void loadUniversities() {
        uniListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                universityListRaw.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Uni uni = snap.getValue(Uni.class);
                    if (uni == null) continue;
                    uni.setId(Integer.parseInt(snap.getKey()));
                    universityListRaw.add(uni);
                }
                universitiesLoaded = true;
                composeAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load universities", Toast.LENGTH_SHORT).show();
            }
        };
        unisRef.addValueEventListener(uniListener);
    }

    private void composeAdapterData() {
        if (!universitiesLoaded || !studentsLoaded || adapter == null) return;

        List<UniversityWithStudents> combinedList = new ArrayList<>();
        for (Uni uni : universityListRaw) {
            List<Student> list = studentMap.get(String.valueOf(uni.getId()));
            if (list == null) {
                list = new ArrayList<>();
            }
            combinedList.add(new UniversityWithStudents(uni, list));
        }

        adapter = new UniversityAdapter(combinedList, getContext());
        universitiesRecyclerView.setAdapter(adapter);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (uniListener != null) unisRef.removeEventListener(uniListener);
        if (studentListener != null) studentsRef.removeEventListener(studentListener);
    }
}
