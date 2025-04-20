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

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Uni;

import java.util.ArrayList;
import java.util.List;

public class AddUniFragment extends DialogFragment {

    private EditText etUniversityName, etLocation, etEstablishedYear, etWebsite, etAssociatedStudentIds, etUniversityPassword;
    private Button btnSubmit;

    public AddUniFragment() {
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_uni, container, false);

        // Initialize all views
        etUniversityName = view.findViewById(R.id.etUniversityName);
        etLocation = view.findViewById(R.id.etLocation);
        etEstablishedYear = view.findViewById(R.id.etEstablishedYear);
        etWebsite = view.findViewById(R.id.etWebsite);
        etAssociatedStudentIds = view.findViewById(R.id.etAssociatedStudentIds);
        etUniversityPassword = view.findViewById(R.id.etUniversityPassword);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String name = etUniversityName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String establishedYearStr = etEstablishedYear.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String associatedStudentIds = etAssociatedStudentIds.getText().toString().trim();
        String uniPassword = etUniversityPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(establishedYearStr) || TextUtils.isEmpty(uniPassword)) {
            Toast.makeText(getContext(), "Name, year and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int establishedYear = Integer.parseInt(establishedYearStr);

            Uni university = new Uni();
            university.name = name;
            university.location = location.isEmpty() ? null : location;
            university.establishedYear = establishedYear;
            university.website = website.isEmpty() ? null : website;
            university.uniPassword = uniPassword;
            university.setAssociatedStudentIdList(parseStudentIds(associatedStudentIds));

            insertUniversityIntoDatabase(university);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid year format", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Integer> parseStudentIds(String rawInput) {
        List<Integer> result = new ArrayList<>();
        if (!TextUtils.isEmpty(rawInput)) {
            for (String id : rawInput.split(",")) {
                try {
                    result.add(Integer.parseInt(id.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }

    private void insertUniversityIntoDatabase(Uni university) {
        new Thread(() -> {
            try {
                long result = DatabaseClient.getInstance(getContext())
                        .getDatabase()
                        .uniDao()
                        .insert(university);

                requireActivity().runOnUiThread(() -> {
                    if (result > 0) {
                        Toast.makeText(getContext(), "University added", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Insert failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error adding university", Toast.LENGTH_SHORT).show());
            }
        }).start();
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
