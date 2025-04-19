package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.UniflowDB;
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

        // Initialize error TextViews
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
            try {
                // Clear previous errors
                clearAllErrors();

                String idStr = idEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String firstname = firstnameEditText.getText().toString().trim();
                String ageStr = ageEditText.getText().toString().trim();
                String telephone = telephoneEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String universityIdStr = universityIdEditText.getText().toString().trim();

                if (validateInputs(idStr, name, firstname, ageStr, telephone, password, email, universityIdStr)) {
                    int id = Integer.parseInt(idStr);
                    int age = Integer.parseInt(ageStr);
                    int universityId = Integer.parseInt(universityIdStr);

                    // Disable button during registration
                    findViewById(R.id.signup_button).setEnabled(false);

                    executorService.execute(() -> registerUser(id, name, firstname, age, telephone, password, email, universityId));
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                findViewById(R.id.signup_button).setEnabled(true);
            }
        });
    }

    private void clearAllErrors() {
        errorId.setVisibility(View.GONE);
        errorName.setVisibility(View.GONE);
        errorFirstname.setVisibility(View.GONE);
        errorAge.setVisibility(View.GONE);
        errorTelephone.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
        errorUniversityId.setVisibility(View.GONE);
    }

    private boolean validateInputs(String idStr, String name, String firstname, String ageStr,
                                   String telephone, String password, String email, String universityIdStr) {
        boolean isValid = true;

        // Validate ID (8 digits)
        if (idStr.length() != 8 || !idStr.matches("\\d+")) {
            errorId.setText("ID must be exactly 8 digits");
            errorId.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate name (letters only, min 2 chars)
        if (name.isEmpty() || !name.matches("[a-zA-Z ]+") || name.length() < 2) {
            errorName.setText("Enter valid name (letters only, min 2 chars)");
            errorName.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate first name (letters only, min 2 chars)
        if (firstname.isEmpty() || !firstname.matches("[a-zA-Z ]+") || firstname.length() < 2) {
            errorFirstname.setText("Enter valid first name (letters only, min 2 chars)");
            errorFirstname.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate age (18-100)
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 100) {
                errorAge.setText("Age must be between 18 and 100");
                errorAge.setVisibility(View.VISIBLE);
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errorAge.setText("Enter a valid age");
            errorAge.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate telephone (10-15 digits)
        if (telephone.isEmpty() || !telephone.matches("\\d{8}")) {
            errorTelephone.setText("Enter valid phone number (10-15 digits)");
            errorTelephone.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate password (min 6 chars)
        if (password.length() < 6) {
            errorPassword.setText("Password must be at least 6 characters");
            errorPassword.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorEmail.setText("Enter valid email address");
            errorEmail.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate university ID (must be a number)
        if (universityIdStr.isEmpty() || !universityIdStr.matches("\\d+")) {
            errorUniversityId.setText("Enter valid university ID");
            errorUniversityId.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    private void registerUser(int id, String name, String firstname, int age, String telephone,
                              String password, String email, int universityId) {
        try {
            UniflowDB database = DatabaseClient.getInstance(this).getDatabase();

            // Check if ID already exists
            if (database.studentDao().getStudentById(id) != null) {
                runOnUiThread(() -> {
                    errorId.setText("ID already exists");
                    errorId.setVisibility(View.VISIBLE);
                    findViewById(R.id.signup_button).setEnabled(true);
                });
                return;
            }

            // Check if email already exists
            if (database.studentDao().findByEmail(email) != null) {
                runOnUiThread(() -> {
                    errorEmail.setText("Email already registered");
                    errorEmail.setVisibility(View.VISIBLE);
                    findViewById(R.id.signup_button).setEnabled(true);
                });
                return;
            }

            // Create new student
            Student student = new Student(id, email, name + " " + firstname,
                    age, telephone, universityId, password);
            student.isOnline = true;
            student.lastLogin = dateFormat.format(new Date());

            // Insert student
            database.studentDao().setAllOffline();
            database.studentDao().insert(student);

            runOnUiThread(() -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                findViewById(R.id.signup_button).setEnabled(true);
            });
            e.printStackTrace();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}