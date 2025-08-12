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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.R;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;

import java.util.   ArrayList;
import java.util.List;

public class AddStudentFragment extends DialogFragment {

    private TextInputEditText etFullName, etEmail, etPassword, etAge, etTelephone, etUniversityId, etCIN;
    private MaterialButton btnSubmitStudent;

    private final DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
    private final DatabaseReference unisRef = FirebaseDatabase.getInstance().getReference("universities");

    public AddStudentFragment() {
        setStyle(STYLE_NORMAL, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);

        etFullName     = view.findViewById(R.id.etFullName);
        etEmail        = view.findViewById(R.id.etEmail);
        etPassword     = view.findViewById(R.id.etPassword);
        etAge          = view.findViewById(R.id.etAge);
        etTelephone    = view.findViewById(R.id.etTelephone);
        etUniversityId = view.findViewById(R.id.etUniversityId);
        etCIN          = view.findViewById(R.id.etCIN); // NEW FIELD for CIN as ID

        btnSubmitStudent = view.findViewById(R.id.btnSubmitStudent);

        btnSubmitStudent.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String fullName  = etFullName.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String ageStr    = etAge.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String uniId     = etUniversityId.getText().toString().trim();
        String cin       = etCIN.getText().toString().trim(); // CIN as ID

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(ageStr)
                || TextUtils.isEmpty(telephone) || TextUtils.isEmpty(uniId)
                || TextUtils.isEmpty(cin)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cin.matches("\\d+")) {
            Toast.makeText(getContext(), "CIN must be numeric", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        validateUniversityExists(uniId, cin, fullName, email, password, age, telephone);
    }

    private void validateUniversityExists(String uniId, String cin, String fullName,
                                          String email, String password, int age, String telephone) {
        unisRef.child(uniId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "University ID not found!", Toast.LENGTH_SHORT).show();
                } else {
                    Uni uni = snapshot.getValue(Uni.class);
                    if (uni == null || uni.getName() == null) {
                        Toast.makeText(getContext(), "Invalid university data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Create tags list with the university name
                    List<String> tags = new ArrayList<>();
                    tags.add(uni.getName());

                    insertStudentIntoFirebase(cin, fullName, email, password, age, telephone, uniId, tags);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to validate university", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertStudentIntoFirebase(String cin, String fullName, String email, String password,
                                           int age, String telephone, String uniId, List<String> tags) {

        Student student = new Student(email, fullName, age, telephone, uniId, password);
        student.setId(cin); // ✅ CIN as ID
        student.setTags(tags); // ✅ Assign default tags

        studentsRef.child(cin).setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Student Added Successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error inserting student: " + e.getMessage(), Toast.LENGTH_SHORT).show()
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
