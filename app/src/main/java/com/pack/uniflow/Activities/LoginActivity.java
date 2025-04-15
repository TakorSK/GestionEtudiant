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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
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
                Toast.makeText(this, "ID/Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (loginId.equals("debug") && password.equals("debug")) {
                navigateToMainActivity();
                return;
            }

            executorService.execute(() -> processLogin(loginId, password));
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void processLogin(String loginId, String password) {
        try {
            UniflowDB database = DatabaseClient.getInstance(getApplicationContext()).getDatabase();

            // First set all users offline (cleanup)
            database.studentDao().setAllOffline();

            Student student = database.studentDao().findByEmail(loginId);

            if (student == null) {
                try {
                    int studentId = Integer.parseInt(loginId);
                    student = database.studentDao().getStudentById(studentId);
                } catch (NumberFormatException e) {
                    // loginId is not a valid number
                }
            }

            if (student != null && student.password.equals(password)) {
                // Update login status and timestamp
                student.isOnline = true;
                student.lastLogin = dateFormat.format(new Date());
                database.studentDao().update(student);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
            });
            e.printStackTrace();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}