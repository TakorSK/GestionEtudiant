package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pack.uniflow.Club;
import com.pack.uniflow.R;

public class AddClubFragment extends DialogFragment {

    private TextInputEditText etClubName, etDescription, etUniversityId;
    private MaterialButton btnSubmitClub;

    private final DatabaseReference clubsRef = FirebaseDatabase.getInstance().getReference("clubs");

    public AddClubFragment() {
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_club, container, false);

        etClubName = view.findViewById(R.id.etClubName);
        etDescription = view.findViewById(R.id.etDescription);
        etUniversityId = view.findViewById(R.id.etUniversityId);
        btnSubmitClub = view.findViewById(R.id.btnSubmitClub);

        btnSubmitClub.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String name = etClubName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String uniId = etUniversityId.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(uniId)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Club club = new Club();
        club.setName(name);
        club.setDescription(description.isEmpty() ? null : description);
        club.setUniId(uniId);

        insertClubIntoFirebase(club);
    }

    private void insertClubIntoFirebase(Club club) {
        String newId = clubsRef.push().getKey();
        if (newId == null) {
            Toast.makeText(getContext(), "Failed to generate club ID", Toast.LENGTH_SHORT).show();
            return;
        }
        club.setId(newId);

        clubsRef.child(newId).setValue(club)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Club Added Successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error inserting club", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
