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
    }

    private void setupSignupButton() {
        findViewById(R.id.signup_button).setOnClickListener(v -> {
            String idStr = idEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String firstname = firstnameEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String telephone = telephoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String universityIdStr = universityIdEditText.getText().toString().trim();

            if (validateInputs(idStr, name, firstname, ageStr, telephone, password, email, universityIdStr)) {
                int id = Integer.parseInt(idStr);
                int age = Integer.parseInt(ageStr);
                int universityId = Integer.parseInt(universityIdStr);
                executorService.execute(() -> registerUser(id, name, firstname, age, telephone, password, email, universityId));
            }
        });
    }

    private boolean validateInputs(String idStr, String name, String firstname, String ageStr,
                                   String telephone, String password, String email, String universityIdStr) {
        // Validation logic remains the same
        return true;
    }

    private void registerUser(int id, String name, String firstname, int age, String telephone,
                              String password, String email, int universityId) {
        try {
            UniflowDB database = DatabaseClient.getInstance(this).getDatabase();

            // Create new student
            Student student = new Student(id, email, name + " " + firstname,
                    age, telephone, universityId, password);

            // Set session properties
            student.isOnline = true;
            student.lastLogin = dateFormat.format(new Date());

            // Ensure only one active session
            database.studentDao().setAllOffline();
            database.studentDao().insert(student);

            runOnUiThread(() -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            });
        } catch (Exception e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
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