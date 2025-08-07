package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Adapters.StudentAdapter;
import com.pack.uniflow.Adapters.UniversityAdapter;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private RecyclerView resultsRecyclerView;
    private LinearLayout resultsSection;
    private TextView noResultsText;
    private EditText searchEditText; // EditText for search input

    private StudentAdapter studentAdapter;
    private UniversityAdapter universityAdapter;
    private List<Student> studentList = new ArrayList<>();
    private List<Uni> universityList = new ArrayList<>();

    // Test data initialization (you can remove this later and fetch real data)
    private void initializeTestData() {
        studentList.add(new Student("john.doe@example.com", "John Doe", 20, "1234567890", "uni123", "password123"));
        studentList.add(new Student("jane.smith@example.com", "Jane Smith", 22, "9876543210", "uni123", "password456"));
        studentList.add(new Student("alex.brown@example.com", "Alex Brown", 21, "5555555555", "uni124", "password789"));

        universityList.add(new Uni("University 123", "New York", 1990, "www.uni123.com", "uni123password"));
        universityList.add(new Uni("University 124", "Los Angeles", 1985, "www.uni124.com", "uni124password"));
        universityList.add(new Uni("University 125", "Chicago", 2000, "www.uni125.com", "uni125password"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize views
        resultsRecyclerView = rootView.findViewById(R.id.resultsRecyclerView);
        resultsSection = rootView.findViewById(R.id.resultsSection);
        noResultsText = rootView.findViewById(R.id.noResultsText);
        searchEditText = rootView.findViewById(R.id.searchEditText); // Use the EditText

        // Set up the RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the test data
        initializeTestData();

        // Add a TextWatcher to handle search text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                performSearch(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
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
