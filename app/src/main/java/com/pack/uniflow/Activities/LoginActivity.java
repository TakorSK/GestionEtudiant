package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pack.uniflow.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    // SharedPreferences key for storing login status
    public static final String PREFS_NAME = "MyPrefs";
    public static final String IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_cid_gmail);
        passwordEditText = findViewById(R.id.login_password);

        // Check if the user is already logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(IS_LOGGED_IN, false);
        if (isLoggedIn) {
            navigateToMainActivity();  // Automatically go to MainActivity if already logged in
        }

        // Handle login button click

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // THIS IS A MOCK LOGIN TEST PLEASE DELETE THIS LATER WHEN IMPLEMENTING THE DATABASE AND LINKING IT! ASK ME TO REMOVE IT
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (validateLogin(email, password)) {
                    // Store login status in SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(IS_LOGGED_IN, true);  // User is logged in
                    editor.apply();

                    // Navigate to MainActivity after successful login
                    navigateToMainActivity();
                } else {
                    // Show an error message if login fails
                    Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateLogin(String email, String password) {
        // Mock validation (replace with actual validation logic)
        return email.equals("test@example.com") && password.equals("password123");
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close LoginActivity so the user can't return to it
    }
}
