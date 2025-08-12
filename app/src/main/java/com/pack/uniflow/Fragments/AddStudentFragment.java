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

public class AddStudentFragment extends DialogFragment {

    private TextInputEditText etFullName, etEmail, etPassword, etAge, etTelephone, etUniversityId;
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

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etAge = view.findViewById(R.id.etAge);
        etTelephone = view.findViewById(R.id.etTelephone);
        etUniversityId = view.findViewById(R.id.etUniversityId);
        btnSubmitStudent = view.findViewById(R.id.btnSubmitStudent);

        btnSubmitStudent.setOnClickListener(v -> handleSubmit());

        return view;
    }

    private void handleSubmit() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String uniId = etUniversityId.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(ageStr)
                || TextUtils.isEmpty(telephone) || TextUtils.isEmpty(uniId)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        validateUniversityExists(uniId, fullName, email, password, age, telephone);
    }

    private void validateUniversityExists(String uniId, String fullName, String email, String password, int age, String telephone) {
        unisRef.child(uniId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "University ID not found!", Toast.LENGTH_SHORT).show();
                } else {
                    insertStudentIntoFirebase(fullName, email, password, age, telephone, uniId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to validate university", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertStudentIntoFirebase(String fullName, String email, String password, int age, String telephone, String uniId) {
        String newId = studentsRef.push().getKey();
        if (newId == null) {
            Toast.makeText(getContext(), "Failed to generate student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(email, fullName, age, telephone, uniId, password);
        student.setId(newId);

        studentsRef.child(newId).setValue(student)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Student Added Successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error inserting student", Toast.LENGTH_SHORT).show()
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
