package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    private EditText idEditText, nameEditText, firstnameEditText, ageEditText,
            telephoneEditText, passwordEditText, emailEditText, universityIdEditText;
    private TextView errorId, errorName, errorFirstname, errorAge,
            errorTelephone, errorPassword, errorEmail, errorUniversityId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference unisRef = database.getReference("universities");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();
        setupSignupButton();

    }

    private void initializeViews() {
        idEditText = findViewById(R.id.signup_id);
        nameEditText = findViewById(R.id.signup_name);
        firstnameEditText = findViewById(R.id.signup_firstname);
        ageEditText = findViewById(R.id.signup_age);
        telephoneEditText = findViewById(R.id.signup_telephone);
        passwordEditText = findViewById(R.id.signup_password);
        emailEditText = findViewById(R.id.signup_email);
        universityIdEditText = findViewById(R.id.signup_university_id);

        errorId = findViewById(R.id.error_signup_id);
        errorName = findViewById(R.id.error_signup_name);
        errorFirstname = findViewById(R.id.error_signup_firstname);
        errorAge = findViewById(R.id.error_signup_age);
        errorTelephone = findViewById(R.id.error_signup_telephone);
        errorPassword = findViewById(R.id.error_signup_password);
        errorEmail = findViewById(R.id.error_signup_email);
        errorUniversityId = findViewById(R.id.error_signup_university_id);
    }

    private void setupSignupButton() {
        findViewById(R.id.signup_button).setOnClickListener(v -> {
            clearAllErrors();
            String studentNumber = idEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String firstname = firstnameEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String telephone = telephoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String universityCode = universityIdEditText.getText().toString().trim();

            if (validateInputs(studentNumber, name, firstname, ageStr, telephone, password, email, universityCode)) {
                findViewById(R.id.signup_button).setEnabled(false);
                executorService.execute(() -> processRegistration(
                        studentNumber, name, firstname, ageStr, telephone, password, email, universityCode
                ));
            }
        });
    }

    private void processRegistration(String studentId, String name, String firstname, String ageStr,
                                     String telephone, String password, String email, String universityId) {
        try {
            final int age = Integer.parseInt(ageStr); // Made final
            final String fullName = name + " " + firstname; // Made final

            // 1. Check university exists
            unisRef.child(universityId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Uni university = dataSnapshot.getValue(Uni.class); // Made final
                    if (university == null) {
                        showErrorOnUI(errorUniversityId, "University not found");
                        return;
                    }

                    // 2. Check student ID is authorized
                    if (!university.getAssociatedStudentIds().contains(studentId)) {
                        showErrorOnUI(errorId, "ID not authorized for this university");
                        return;
                    }

                    // 3. Check if student ID already exists
                    studentsRef.orderByKey().equalTo(studentId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        showErrorOnUI(errorId, "ID already registered");
                                        return;
                                    }

                                    // 4. Check if email already exists
                                    studentsRef.orderByChild("email").equalTo(email)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        showErrorOnUI(errorEmail, "Email already registered");
                                                        return;
                                                    }

                                                    // 5. Create and save new student
                                                    Student student = new Student(
                                                            email,
                                                            fullName, // Using final variable
                                                            age,     // Using final variable
                                                            telephone,
                                                            universityId,
                                                            password
                                                    );
                                                    student.setId(studentId);
                                                    student.setOnline(true);
                                                    student.setLastLogin(dateFormat.format(new Date()));

                                                    studentsRef.child(studentId).setValue(student)
                                                            .addOnSuccessListener(aVoid -> {
                                                                runOnUiThread(() -> {
                                                                    Toast.makeText(SignupActivity.this,
                                                                            "Registration successful!", Toast.LENGTH_SHORT).show();
                                                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                    editor.putBoolean("is_logged_in", true);
                                                                    editor.putString("LOGIN_TYPE", "REGULAR_STUDENT"); // Or handle STUDENT_ADMIN logic here
                                                                    editor.putString("UNIVERSITY_ID", universityId);
                                                                    editor.putString("STUDENT_ID", studentId);
                                                                    editor.apply();

                                                                    startActivity(new Intent(SignupActivity.this, MainActivity.class)
                                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                    finish();

                                                                });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                runOnUiThread(() -> {
                                                                    Toast.makeText(SignupActivity.this,
                                                                            "Registration error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                    findViewById(R.id.signup_button).setEnabled(true);
                                                                });
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    runOnUiThread(() -> showDatabaseError(databaseError));
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    runOnUiThread(() -> showDatabaseError(databaseError));
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    runOnUiThread(() -> showDatabaseError(databaseError));
                }
            });
        } catch (NumberFormatException e) {
            showErrorOnUI(errorAge, "Invalid age");
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(SignupActivity.this,
                        "Registration error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                findViewById(R.id.signup_button).setEnabled(true);
            });
        }
    }

    private void showDatabaseError(DatabaseError databaseError) {
        Toast.makeText(SignupActivity.this,
                "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
        findViewById(R.id.signup_button).setEnabled(true);
    }



    private boolean validateInputs(String studentNumber, String name, String firstname, String ageStr,
                                   String telephone, String password, String email, String universityCode) {
        boolean isValid = true;

        // Student Number Validation (8 digits)
        if (studentNumber.length() != 8 || !studentNumber.matches("\\d+")) {
            showError(errorId, "ID must be 8 digits");
            isValid = false;
        }

        // University Code Validation
        if (universityCode.isEmpty() || !universityCode.matches("\\d+")) {
            showError(errorUniversityId, "Enter valid university code");
            isValid = false;
        }

        // Name Validation
        if (name.isEmpty() || !name.matches("[a-zA-Z ]+") || name.length() < 2) {
            showError(errorName, "Enter valid name (letters only, min 2 chars)");
            isValid = false;
        }

        // Firstname Validation
        if (firstname.isEmpty() || !firstname.matches("[a-zA-Z ]+") || firstname.length() < 2) {
            showError(errorFirstname, "Enter valid first name");
            isValid = false;
        }

        // Age Validation
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 100) {
                showError(errorAge, "Age must be 18-100");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            showError(errorAge, "Enter valid age");
            isValid = false;
        }

        // Telephone Validation
        if (telephone.isEmpty() || !telephone.matches("\\d{8,15}")) {
            showError(errorTelephone, "Enter valid phone (8-15 digits)");
            isValid = false;
        }

        // Password Validation
        if (password.length() < 6) {
            showError(errorPassword, "Password must be ≥6 characters");
            isValid = false;
        }

        // Email Validation
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(errorEmail, "Enter valid email");
            isValid = false;
        }

        return isValid;
    }

    private void showError(TextView errorView, String message) {
        runOnUiThread(() -> {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        });
    }

    private void showErrorOnUI(TextView errorView, String message) {
        runOnUiThread(() -> {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
            findViewById(R.id.signup_button).setEnabled(true);
        });
    }

    private void clearAllErrors() {
        runOnUiThread(() -> {
            errorId.setVisibility(View.GONE);
            errorName.setVisibility(View.GONE);
            errorFirstname.setVisibility(View.GONE);
            errorAge.setVisibility(View.GONE);
            errorTelephone.setVisibility(View.GONE);
            errorPassword.setVisibility(View.GONE);
            errorEmail.setVisibility(View.GONE);
            errorUniversityId.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}