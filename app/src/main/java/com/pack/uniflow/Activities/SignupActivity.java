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
            String idStr = idEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String firstname = firstnameEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String telephone = telephoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String universityIdStr = universityIdEditText.getText().toString().trim();

            if (validateInputs(idStr, name, firstname, ageStr, telephone, password, email, universityIdStr)) {
                findViewById(R.id.signup_button).setEnabled(false);
                executorService.execute(() -> processRegistration(
                        idStr, name, firstname, ageStr, telephone, password, email, universityIdStr
                ));
            }
        });
    }

    private void processRegistration(String idStr, String name, String firstname, String ageStr,
                                     String telephone, String password, String email, String universityIdStr) {
        try {
            int id = Integer.parseInt(idStr);
            int age = Integer.parseInt(ageStr);
            int universityId = Integer.parseInt(universityIdStr);

            UniflowDB database = DatabaseClient.getInstance(this).getDatabase();
            Uni university = database.uniDao().getUniversityById(universityId);

            if (university == null) {
                showErrorOnUI(errorUniversityId, "University not found");
                return;
            }

            if (!university.getAssociatedStudentIdList().contains(id)) {
                showErrorOnUI(errorId, "ID not authorized for this university");
                return;
            }

            if (database.studentDao().getStudentById(id) != null) {
                showErrorOnUI(errorId, "ID already registered");
                return;
            }

            if (database.studentDao().findByEmail(email) != null) {
                showErrorOnUI(errorEmail, "Email already registered");
                return;
            }

            Student student = new Student(id, email, name + " " + firstname,
                    age, telephone, universityId, password);
            student.isOnline = true;
            student.lastLogin = dateFormat.format(new Date());

            database.studentDao().setAllOffline();
            database.studentDao().insert(student);

            runOnUiThread(() -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            });

        } catch (NumberFormatException e) {
            showErrorOnUI(errorUniversityId, "Invalid university ID");
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Registration error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                findViewById(R.id.signup_button).setEnabled(true);
            });
        }
    }

    private boolean validateInputs(String idStr, String name, String firstname, String ageStr,
                                   String telephone, String password, String email, String universityIdStr) {
        boolean isValid = true;

        // ID Validation (8 digits)
        if (idStr.length() != 8 || !idStr.matches("\\d+")) {
            showError(errorId, "ID must be 8 digits");
            isValid = false;
        }

        // University ID Validation
        if (universityIdStr.isEmpty() || !universityIdStr.matches("\\d+")) {
            showError(errorUniversityId, "Enter valid university ID");
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