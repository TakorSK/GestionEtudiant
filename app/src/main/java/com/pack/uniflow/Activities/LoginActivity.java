package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText loginIdEditText, passwordEditText;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference unisRef = database.getReference("universities");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupLoginButton();
        setupSignupButton();

        // Auto-login if already logged in
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("is_logged_in", false)) {
            String studentId = prefs.getString("STUDENT_ID", null);
            String loginTypeStr = prefs.getString("LOGIN_TYPE", null);
            String universityId = prefs.getString("UNIVERSITY_ID", null);

            if (loginTypeStr != null) {
                LoginType loginType = LoginType.valueOf(loginTypeStr);
                startMainActivity(loginType, universityId, studentId);
            }
        }
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

            authenticateUser(loginId, password);
        });
    }

    private void setupSignupButton() {
        findViewById(R.id.go_to_signup_button).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void authenticateUser(String loginId, String password) {
        // Debug admin login
        if ("debug".equals(loginId) && "debug".equals(password)) {
            Toast.makeText(this, "Developer admin login", Toast.LENGTH_SHORT).show();
            startMainActivity(LoginType.DEBUG_ADMIN, null, null);
            return;
        }

        // University admin login
        unisRef.child(loginId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Uni uni = snapshot.getValue(Uni.class);
                if (uni != null && uni.getUniPassword() != null && uni.getUniPassword().equals(password)) {
                    Toast.makeText(LoginActivity.this, "University admin login", Toast.LENGTH_SHORT).show();
                    startMainActivity(LoginType.UNIVERSITY_ADMIN, loginId, null);
                } else {
                    // Try student login if university admin fails
                    checkStudentLogin(loginId, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStudentLogin(String loginId, String password) {
        // First try to match by email
        studentsRef.orderByChild("email").equalTo(loginId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        validateStudent(snap, password);
                        return;
                    }
                } else {
                    // Try with ID as key
                    studentsRef.child(loginId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                validateStudent(snapshot, password);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateStudent(DataSnapshot snapshot, String password) {
        Student student = snapshot.getValue(Student.class);
        if (student != null && student.getPassword() != null && student.getPassword().equals(password)) {
            student.setId(snapshot.getKey());
            student.setOnline(true);
            student.setLastLogin(dateFormat.format(new Date()));
            studentsRef.child(snapshot.getKey()).setValue(student);

            Toast.makeText(this, "Student login successful", Toast.LENGTH_SHORT).show();
            LoginType type = student.isAdmin() ? LoginType.STUDENT_ADMIN : LoginType.REGULAR_STUDENT;
            startMainActivity(type, student.getUniId(), student.getId());
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity(LoginType loginType, String universityId, String studentId) {
        // Save login state
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("LOGIN_TYPE", loginType.name());
        editor.putString("UNIVERSITY_ID", universityId);
        editor.putString("STUDENT_ID", studentId);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LOGIN_TYPE", loginType.name());
        intent.putExtra("UNIVERSITY_ID", universityId);
        intent.putExtra("STUDENT_ID", studentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public enum LoginType {
        DEBUG_ADMIN,
        UNIVERSITY_ADMIN,
        STUDENT_ADMIN,
        REGULAR_STUDENT
    }
}
