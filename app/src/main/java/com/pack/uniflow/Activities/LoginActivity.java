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

    private EditText emailEditText, passwordEditText;
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
        emailEditText = findViewById(R.id.login_cid_gmail);
        passwordEditText = findViewById(R.id.login_password);
    }

    private void setupLoginButton() {
        findViewById(R.id.login_button).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Email and password are required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Debug bypass (remove in production)
            if (email.equals("debug") && password.equals("debug")) {
                navigateToMainActivity();
                return;
            }

            executorService.execute(() -> processLogin(email, password));
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void processLogin(String email, String password) {
        try {
            UniflowDB database = DatabaseClient.getInstance(getApplicationContext()).getDatabase();
            Student student = database.studentDao().findByEmail(email);

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
                            "Invalid email or password",
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}