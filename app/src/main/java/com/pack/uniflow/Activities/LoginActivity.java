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
    public static final String PREFS_NAME = "UserPrefs"; // Make sure the name is consistent with SignupActivity
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
                String email = emailEditText.getText().toString(); // Email or ID entered by user
                String password = passwordEditText.getText().toString(); // Password entered by user

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

        // Add OnClickListener for the Sign Up button
        findViewById(R.id.go_to_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Validate login credentials
    private boolean validateLogin(String email, String password) {
        // Retrieve user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString("EMAIL", "");  // Get saved email from signup
        String savedId = prefs.getString("ID", "");  // Get saved ID from signup
        String savedPassword = prefs.getString("PASSWORD", "");  // Get saved password from signup

        // Check if entered email or ID matches the saved email/ID, and password matches the saved one
        return (email.equals(savedEmail) || email.equals(savedId)) && password.equals(savedPassword);
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close LoginActivity so the user can't return to it
    }
}
