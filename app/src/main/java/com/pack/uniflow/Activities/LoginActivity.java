package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.UniflowDB;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText loginIdEditText, passwordEditText;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupLoginButton();
        setupSignupButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void initializeViews() {
        loginIdEditText = findViewById(R.id.login_cid_gmail); // Should contain either email or ID
        passwordEditText = findViewById(R.id.login_password);
    }

    private void setupLoginButton() {
        findViewById(R.id.login_button).setOnClickListener(v -> {
            String loginId = loginIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (loginId.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "ID/Email and password are required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Debug bypass (remove in production)
            if (loginId.equals("debug") && password.equals("debug")) {
                navigateToMainActivity();
                return;
            }

            executorService.execute(() -> processLogin(loginId, password));
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish(); // Optional: close login activity if you don't want it in back stack
        });
    }

    private void processLogin(String loginId, String password) {
        try {
            UniflowDB database = DatabaseClient.getInstance(getApplicationContext()).getDatabase();

            // First try to find by email
            Student student = database.studentDao().findByEmail(loginId);

            // If not found by email, try to find by ID
            if (student == null) {
                try {
                    int studentId = Integer.parseInt(loginId);
                    student = database.studentDao().getStudentById(studentId);
                } catch (NumberFormatException e) {
                    // loginId is not a valid number
                }
            }

            if (student != null && student.password.equals(password)) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this,
                            "Login successful!",
                            Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this,
                            "Invalid ID/Email or password",
                            Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this,
                        "Login failed. Please try again.",
                        Toast.LENGTH_SHORT).show();
            });
            e.printStackTrace();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Don't call finish() here - let the activity stay in stack if needed
    }
}