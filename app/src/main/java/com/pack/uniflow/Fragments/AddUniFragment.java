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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;

import java.util.ArrayList;
import java.util.List;

public class AddUniFragment extends DialogFragment {

    private EditText etUniversityName,
            etLocation,
            etEstablishedYear,
            etWebsite,
            etAssociatedStudentIds,
            etUniversityPassword;
    private Button btnSubmit;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference unisRef = database.getReference("universities");
    private final DatabaseReference counterRef = database.getReference("counters/university");

    public AddUniFragment() {
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_uni, container, false);

        etUniversityName       = view.findViewById(R.id.etUniversityName);
        etLocation             = view.findViewById(R.id.etLocation);
        etEstablishedYear      = view.findViewById(R.id.etEstablishedYear);
        etWebsite              = view.findViewById(R.id.etWebsite);
        etAssociatedStudentIds = view.findViewById(R.id.etAssociatedStudentIds);
        etUniversityPassword   = view.findViewById(R.id.etUniversityPassword);
        btnSubmit              = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> handleSubmit());
        return view;
    }

    private void handleSubmit() {
        String name               = etUniversityName.getText().toString().trim();
        String location           = etLocation.getText().toString().trim();
        String establishedYearStr = etEstablishedYear.getText().toString().trim();
        String website            = etWebsite.getText().toString().trim();
        String associatedIdsRaw   = etAssociatedStudentIds.getText().toString().trim();
        String uniPassword        = etUniversityPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(establishedYearStr) || TextUtils.isEmpty(uniPassword)) {
            Toast.makeText(getContext(), "Name, year and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int establishedYear = Integer.parseInt(establishedYearStr);

            Uni university = new Uni(name,
                    location.isEmpty() ? null : location,
                    establishedYear,
                    website.isEmpty() ? null : website,
                    uniPassword);

            university.setAssociatedStudentIds(parseStudentIds(associatedIdsRaw));

            insertUniversityWithAutoIncrement(university);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid year format", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> parseStudentIds(String rawInput) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(rawInput)) {
            for (String id : rawInput.split(",")) {
                String trimmed = id.trim();
                if (!trimmed.isEmpty()) result.add(trimmed);
            }
        }
        return result;
    }

    private void insertUniversityWithAutoIncrement(Uni university) {
        counterRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long currentCount = currentData.getValue(Long.class);
                if (currentCount == null) {
                    currentCount = 1L;
                } else {
                    currentCount += 1;
                }
                currentData.setValue(currentCount);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot snapshot) {
                if (committed && snapshot != null) {
                    int newId = snapshot.getValue(int.class);
                    int idString =newId;
                    university.setId(idString);

                    unisRef.child(String.valueOf(idString)).setValue(university)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "University added with ID: " + idString, Toast.LENGTH_SHORT).show();
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Insert failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getContext(), "Failed to generate university ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
