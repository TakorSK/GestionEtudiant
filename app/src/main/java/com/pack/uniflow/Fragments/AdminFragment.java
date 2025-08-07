package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.pack.uniflow.Adapters.StudentAdapter;
import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
import com.pack.uniflow.StudentDao;
import com.pack.uniflow.UniDao;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private RecyclerView resultsRecyclerView;
    private LinearLayout resultsSection;
    private TextView noResultsText;

    private StudentAdapter studentAdapter;
    private UniversityAdapter universityAdapter;
    private List<Student> studentList = new ArrayList<>();
    private List<Uni> universityList = new ArrayList<>();

    private StudentDao studentDao;
    private UniDao uniDao;

    public AdminFragment() {
        // Initialize DAO classes for Firebase data access
        studentDao = new StudentDao();
        uniDao = new UniDao();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Firebase persistence when the fragment is created
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Enable persistence
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize views
        resultsRecyclerView = rootView.findViewById(R.id.resultsRecyclerView);
        resultsSection = rootView.findViewById(R.id.resultsSection);
        noResultsText = rootView.findViewById(R.id.noResultsText);

        // Set up the RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch real data from Firebase
        fetchUniversities();
        fetchStudents();

        // Set up the SearchView
        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setQueryHint("Search universities (default), or student:john");

        // Set up SearchView listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        return rootView;
    }

    private void fetchUniversities() {
        uniDao.getAllUnis(new UniDao.LoadCallback() {
            @Override
            public void onLoaded(List<Uni> unis) {
                universityList = unis;
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching universities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudents() {
        studentDao.getAllStudents(new StudentDao.LoadCallback() {
            @Override
            public void onLoaded(List<Student> students) {
                studentList = students;
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error fetching students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        // Show the results section
        resultsSection.setVisibility(View.VISIBLE);

        // If the query starts with "uni:", search for universities
        if (query.startsWith("uni:")) {
            String searchQuery = query.substring(4).trim(); // Remove 'uni:' prefix
            filterUniversityList(searchQuery);
        }
        // If the query starts with "stu:", search for students
        else if (query.startsWith("stu:")) {
            String searchQuery = query.substring(4).trim(); // Remove 'stu:' prefix
            filterStudentList(searchQuery);
        }
        // Default search (searching universities by name)
        else {
            filterUniversityList(query);
        }
    }

    private void filterUniversityList(String query) {
        List<Uni> filteredList = new ArrayList<>();
        for (Uni uni : universityList) {
            if (uni.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(uni);
            }
        }

        // Update the UI based on search results
        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);  // Show "No results"
        } else {
            noResultsText.setVisibility(View.GONE);     // Hide "No results"
            universityAdapter = new UniversityAdapter(filteredList, getContext());
            resultsRecyclerView.setAdapter(universityAdapter);
            universityAdapter.notifyDataSetChanged();
        }
    }

    private void filterStudentList(String query) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(student);
            }
        }

        // Update the UI based on search results
        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);  // Show "No results"
        } else {
            noResultsText.setVisibility(View.GONE);     // Hide "No results"
            studentAdapter = new StudentAdapter(filteredList, getContext());
            resultsRecyclerView.setAdapter(studentAdapter);
            studentAdapter.notifyDataSetChanged();
        }
    }
}
