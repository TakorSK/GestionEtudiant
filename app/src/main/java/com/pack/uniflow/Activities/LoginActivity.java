package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.StudentDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    Student currentstudent;
    private EditText loginIdEditText, passwordEditText;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        setupLoginButton();
        setupSignupButton();
    }

    private void initializeViews() {
        loginIdEditText = findViewById(R.id.login_cid_gmail);
        passwordEditText = findViewById(R.id.login_password);
    }

    private void setupLoginButton() {
        findViewById(R.id.login_button).setOnClickListener(v -> {
            String loginId = loginIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (loginId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> authenticateUser(loginId, password));
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void authenticateUser(String loginId, String password) {
        try {
            UniflowDB database = DatabaseClient.getInstance(this).getDatabase();

            // 1. Check for debug admin login (special case)
            if ("debug".equals(loginId) && "debug".equals(password)) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Developer admin login", Toast.LENGTH_SHORT).show();
                    startMainActivity(LoginType.DEBUG_ADMIN, -1); // -1 for debug admin
                });
                return;
            }

            // 2. Check for university admin login
            try {
                int universityId = Integer.parseInt(loginId);
                String uniPassword = database.uniDao().getUniPasswordById(universityId);

                if (uniPassword != null && uniPassword.equals(password)) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "University admin login", Toast.LENGTH_SHORT).show();
                        startMainActivity(LoginType.UNIVERSITY_ADMIN, universityId);
                    });
                    return;
                }
            } catch (NumberFormatException e) {
                // Not a university ID, proceed to student login
            }

            // 3. Regular student login
            database.studentDao().setAllOffline(); // Clear existing sessions

            currentstudent = database.studentDao().findByEmail(loginId);
            if (currentstudent == null) {
                try {
                    currentstudent = database.studentDao().getStudentById(Integer.parseInt(loginId));
                } catch (NumberFormatException ignored) {}
            }

            if (currentstudent != null && currentstudent.password.equals(password)) {
                currentstudent.isOnline = true;
                currentstudent.lastLogin = dateFormat.format(new Date());
                database.studentDao().update(currentstudent);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Student login successful", Toast.LENGTH_SHORT).show();
                    startMainActivity(
                            currentstudent.isAdmin ? LoginType.STUDENT_ADMIN : LoginType.REGULAR_STUDENT,
                            currentstudent.uniId // Pass university ID for students too
                    );
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void startMainActivity(LoginType loginType, int universityId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LOGIN_TYPE", loginType.name());
        intent.putExtra("UNIVERSITY_ID", universityId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    public enum LoginType {
        DEBUG_ADMIN,       // Special debug account
        UNIVERSITY_ADMIN,  // University administrator
        STUDENT_ADMIN,     // Student with admin privileges
        REGULAR_STUDENT    // Normal student
    }
}
