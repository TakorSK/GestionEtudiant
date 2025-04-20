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

    private void initializeViews() {
        loginIdEditText = findViewById(R.id.login_cid_gmail);
        passwordEditText = findViewById(R.id.login_password);
    }

    private void setupLoginButton() {
        findViewById(R.id.login_button).setOnClickListener(v -> {
            String loginId = loginIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!loginId.isEmpty() && !password.isEmpty()) {
                executorService.execute(() -> authenticateUser(loginId, password));
            } else {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void authenticateUser(String loginId, String password) {
        // Check if the login credentials are for the "debug" account
        if ("debug".equals(loginId) && "debug".equals(password)) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Login successful (Debug mode)", Toast.LENGTH_SHORT).show();
                startMainActivity();
            });
            return;
        }

        // If not "debug", proceed with the regular authentication logic
        try {
            UniflowDB database = DatabaseClient.getInstance(this).getDatabase();

            // Clear any existing sessions
            database.studentDao().setAllOffline();

            // Find user by email or ID
            Student student = database.studentDao().findByEmail(loginId);
            if (student == null) {
                try {
                    student = database.studentDao().getStudentById(Integer.parseInt(loginId));
                } catch (NumberFormatException ignored) {}
            }

            if (student != null && student.password.equals(password)) {
                // Update session
                student.isOnline = true;
                student.lastLogin = dateFormat.format(new Date());
                database.studentDao().update(student);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show());
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
