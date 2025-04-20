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
import com.pack.uniflow.Club;  // Import the Club entity

public class AddClubFragment extends DialogFragment {

    private EditText etClubName, etDescription, etUniversityId;
    private Button btnSubmitClub;

    public AddClubFragment() {
        // Apply rounded dialog style
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_club, container, false);

        // Initialize views
        etClubName = view.findViewById(R.id.etClubName);
        etDescription = view.findViewById(R.id.etDescription);
        etUniversityId = view.findViewById(R.id.etUniversityId);
        btnSubmitClub = view.findViewById(R.id.btnSubmitClub);

        // Handle submit button click
        btnSubmitClub.setOnClickListener(v -> {
            String clubName = etClubName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String universityIdStr = etUniversityId.getText().toString().trim();

            // Validate fields
            if (TextUtils.isEmpty(clubName) || TextUtils.isEmpty(universityIdStr)) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Parse the university ID to integer
                    int universityId = Integer.parseInt(universityIdStr);

                    // Create a new Club object
                    Club club = new Club();
                    club.name = clubName;
                    club.description = description.isEmpty() ? null : description;  // Allow null for optional fields
                    club.uniId = universityId;

                    // Insert into database using Room
                    insertClubIntoDatabase(club);

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid University ID entered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void insertClubIntoDatabase(Club club) {
        // Run the database operation on a background thread (since Room database operations are asynchronous)
        new Thread(() -> {
            try {
                // Insert the club into the database
                long insertedId = DatabaseClient.getInstance(getContext()).getDatabase()
                        .clubDao().insert(club);

                // If insert is successful, the insertedId should be greater than 0
                if (insertedId > 0) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Club Added Successfully", Toast.LENGTH_SHORT).show();
                        dismiss();  // Close the dialog
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error inserting club", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error inserting club", Toast.LENGTH_SHORT).show();
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
