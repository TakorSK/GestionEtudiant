package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pack.uniflow.R;

public class SignupActivity extends AppCompatActivity {

    private EditText idEditText, nameEditText, firstnameEditText, ageEditText, telephoneEditText, passwordEditText, emailEditText;  // Added email field

    // SharedPreferences key for storing signup details
    public static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize EditTexts
        idEditText = findViewById(R.id.signup_id);
        nameEditText = findViewById(R.id.signup_name);
        firstnameEditText = findViewById(R.id.signup_firstname);
        ageEditText = findViewById(R.id.signup_age);
        telephoneEditText = findViewById(R.id.signup_telephone);
        passwordEditText = findViewById(R.id.signup_password);
        emailEditText = findViewById(R.id.signup_email);

        // Handle Sign Up button click
        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input data
                String id = idEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String firstname = firstnameEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String telephone = telephoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // Validate and save data
                if (validateInput(id, name, firstname, age, telephone, password, email)) {
                    saveUserData(id, name, firstname, age, telephone, password, email);  // Pass email to save method
                    Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();  // Navigate to MainActivity after successful sign up
                } else {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate that none of the fields are empty (including email and password)
    private boolean validateInput(String id, String name, String firstname, String age, String telephone, String password, String email) {
        return !id.isEmpty() && !name.isEmpty() && !firstname.isEmpty() && !age.isEmpty() && !telephone.isEmpty() && !password.isEmpty() && !email.isEmpty();
    }

    // Save user data to SharedPreferences, PLEASE FOR THE LOVE OF GOD.. MAKE THE DATA BASE WORK! GOOD LUCK THIS IS USED TO SAVE TO THE LOCAL DEVICE **ONLY**! CALL ME IF YOU WANNA UNDERSTAND MORE
    private void saveUserData(String id, String name, String firstname, String age, String telephone, String password, String email) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("ID", id);
        editor.putString("NAME", name);
        editor.putString("FIRSTNAME", firstname);
        editor.putString("AGE", age);
        editor.putString("TELEPHONE", telephone);
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", password);
        editor.putBoolean("IS_LOGGED_IN", true);  // Mark user as logged in
        editor.apply();
    }

    // Navigate to MainActivity after successful sign up
    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close the SignupActivity so the user can't go back to it
    }
}
