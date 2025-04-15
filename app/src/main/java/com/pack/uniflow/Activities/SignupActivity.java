package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        findViewById(R.id.signup_button).setOnClickListener(v -> handleSignup());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
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

    private void handleSignup() {
        resetErrorMessages();

        String idStr = idEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String firstname = firstnameEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String universityIdStr = universityIdEditText.getText().toString().trim();

        if (!validateInputs(idStr, name, firstname, ageStr, telephone, password, email, universityIdStr)) {
            return;
        }

        int id = Integer.parseInt(idStr);
        int age = Integer.parseInt(ageStr);
        int universityId = Integer.parseInt(universityIdStr);

        executorService.execute(() -> processSignup(id, name, firstname, age, telephone, password, email, universityId));
    }

    private boolean validateInputs(String idStr, String name, String firstname, String ageStr,
                                   String telephone, String password, String email, String universityIdStr) {
        boolean isValid = true;

        if (idStr.isEmpty()) {
            showError(errorId, "ID is required");
            isValid = false;

        } else if (!idStr.matches("\\d+")) {
            showError(errorId, "ID must be a number");
            isValid = false;
        }

        if (name.isEmpty()) {
            showError(errorName, "Name is required");
            isValid = false;
        }

        if (firstname.isEmpty()) {
            showError(errorFirstname, "First name is required");
            isValid = false;
        }

        if (ageStr.isEmpty()) {
            showError(errorAge, "Age is required");
            isValid = false;

        } else if (!ageStr.matches("\\d+")) {
            showError(errorAge, "Age must be a number");
            isValid = false;
        }

        if (telephone.isEmpty()) {
            showError(errorTelephone, "Telephone is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            showError(errorPassword, "Password is required");
            isValid = false;

        } else if (password.length() < 6) {
            showError(errorPassword, "Password must be at least 6 characters");
            isValid = false;
        }

        if (email.isEmpty()) {
            showError(errorEmail, "Email is required");
            isValid = false;

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(errorEmail, "Invalid email format");
            isValid = false;
        }

        if (universityIdStr.isEmpty()) {
            showError(errorUniversityId, "University ID is required");
            isValid = false;

        } else if (!universityIdStr.matches("\\d+")) {
            showError(errorUniversityId, "University ID must be a number");
            isValid = false;
        }

        return isValid;
    }

    private void processSignup(int id, String name, String firstname, int age, String telephone,
                               String password, String email, int universityId) {
        try {
            UniflowDB database = DatabaseClient.getInstance(getApplicationContext()).getDatabase();

            // Check if university exists (assuming the method is now called getById)
            Uni university = database.uniDao().getById(universityId);
            if (university == null) {
                runOnUiThread(() -> {
                    showError(errorUniversityId, "Invalid University ID");
                    Toast.makeText(SignupActivity.this,
                            "The specified university doesn't exist",
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Check if email exists
            if (database.studentDao().findByEmail(email) != null) {
                runOnUiThread(() -> {
                    showError(errorEmail, "Email already registered");
                    Toast.makeText(SignupActivity.this,
                            "This email is already registered",
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Create student with all required fields
            Student student = new Student(
                    id,
                    email,
                    name + " " + firstname,
                    age,
                    telephone,
                    universityId,
                    password
            );

            database.studentDao().insert(student);

            runOnUiThread(() -> {
                Toast.makeText(SignupActivity.this,
                        "Registration successful!",
                        Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            });

        } catch (Exception e) {
            Log.e("SIGNUP_ERROR", "Database error", e);
            runOnUiThread(() ->
                    Toast.makeText(SignupActivity.this,
                            "Registration failed. Please try again.",
                            Toast.LENGTH_LONG).show());
        }
    }

    private void showError(TextView errorView, String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private void resetErrorMessages() {
        int[] errorViews = {R.id.error_signup_id, R.id.error_signup_name, R.id.error_signup_firstname,
                R.id.error_signup_age, R.id.error_signup_telephone, R.id.error_signup_password,
                R.id.error_signup_email, R.id.error_signup_university_id};

        for (int viewId : errorViews) {
            findViewById(viewId).setVisibility(View.GONE);
        }
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}