package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Activities.MainActivity;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private EditText idEditText, nameEditText, firstnameEditText, ageEditText, telephoneEditText, passwordEditText, emailEditText;

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
                handleSignup();
            }
        });
    }

    private void handleSignup() {
        // Get input data
        String idStr = idEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String firstname = firstnameEditText.getText().toString().trim();
        String ageStr = ageEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Validate and save data
        if (validateInput(idStr, name, firstname, ageStr, telephone, password, email)) {
            int id = Integer.parseInt(idStr);

            // Insert into database in background
            new Thread(() -> {
                // Check if the email already exists
                try{
                Student existingStudent = DatabaseClient.getInstance(getApplicationContext())
                        .getDatabase()
                        .studentDao()
                        .findByEmail(email);

                if (existingStudent != null) {
                    // Email already used
                    runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Email already registered!", Toast.LENGTH_SHORT).show());
                } else {
                    // Create new Student
                    Student student = new Student();
                    student.id = id;
                    student.fullName = name + " " + firstname;
                    student.email = email;
                    student.isOnline = false;
                    student.registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    student.lastLogin = null;
                    student.uniId = 1; // default University id
                    student.clubId = null; // No club assigned
                    student.sectionId = null; // No section assigned

                    // Insert the student
                    DatabaseClient.getInstance(getApplicationContext())
                            .getDatabase()
                            .studentDao()
                            .insert(student);

                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    });
                }
                }catch (Exception e) {
                    Log.e("SIGNUP_ERROR", "Failed to insert student: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(
                            SignupActivity.this,
                            "Signup failed. Check logs.",
                            Toast.LENGTH_LONG
                    ).show());}
            }).start();



        } else {
            Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate that none of the fields are empty
    private boolean validateInput(String id, String name, String firstname, String age, String telephone, String password, String email) {
        return !id.isEmpty() && !name.isEmpty() && !firstname.isEmpty() && !age.isEmpty()
                && !telephone.isEmpty() && !password.isEmpty() && !email.isEmpty();
    }

    // Navigate to MainActivity after successful sign up
    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close the SignupActivity so the user can't come back
    }
}
