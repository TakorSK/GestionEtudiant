package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pack.uniflow.DatabaseClient;  // Import DatabaseClient for Room operations
import com.pack.uniflow.R;
import com.pack.uniflow.Uni;  // Import the Uni entity

import java.util.ArrayList;
import java.util.List;

public class AddUniFragment extends DialogFragment {

    private EditText etUniversityName, etLocation, etEstablishedYear, etWebsite, etAssociatedStudentIds;
    private Button btnSubmit;

    public AddUniFragment() {
        // Apply rounded dialog style
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_uni, container, false);

        // Initialize views
        etUniversityName = view.findViewById(R.id.etUniversityName);
        etLocation = view.findViewById(R.id.etLocation);
        etEstablishedYear = view.findViewById(R.id.etEstablishedYear);
        etWebsite = view.findViewById(R.id.etWebsite);
        etAssociatedStudentIds = view.findViewById(R.id.etAssociatedStudentIds);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Handle submit click
        btnSubmit.setOnClickListener(v -> {
            String name = etUniversityName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String establishedYearStr = etEstablishedYear.getText().toString().trim();
            String website = etWebsite.getText().toString().trim();
            String associatedStudentIds = etAssociatedStudentIds.getText().toString().trim();

            // Validate fields
            if (name.isEmpty() || establishedYearStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Parse the established year to integer
                    int establishedYear = Integer.parseInt(establishedYearStr);

                    // Create a new Uni object
                    Uni university = new Uni();
                    university.name = name;
                    university.location = location.isEmpty() ? null : location;  // Allow null for optional fields
                    university.establishedYear = establishedYear;
                    university.website = website.isEmpty() ? null : website;
                    university.setAssociatedStudentIdList(parseStudentIds(associatedStudentIds));

                    // Insert into database using Room
                    insertUniversityIntoDatabase(university);

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid year entered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private List<Integer> parseStudentIds(String associatedStudentIds) {
        // Converts the student IDs from a comma-separated string to a list of integers
        if (associatedStudentIds.isEmpty()) {
            return null;
        }
        String[] ids = associatedStudentIds.split(",");
        List<Integer> studentIds = new ArrayList<>();
        for (String id : ids) {
            try {
                studentIds.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return studentIds;
    }

    private void insertUniversityIntoDatabase(Uni university) {
        // Run the database operation on a background thread (since Room database operations are asynchronous)
        new Thread(() -> {
            try {
                // Insert the university into the database
                long insertedId = DatabaseClient.getInstance(getContext()).getDatabase()
                        .uniDao().insert(university);

                // If insert is successful, the insertedId should be greater than 0
                if (insertedId > 0) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "University Added", Toast.LENGTH_SHORT).show();
                        dismiss();  // Close the dialog
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error inserting university", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error inserting university", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog width to match parent (for a wider modal)
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
