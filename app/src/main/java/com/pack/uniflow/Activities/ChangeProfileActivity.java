package com.pack.uniflow.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivProfilePicture;
    private EditText edtBio;
    private Button btnSave;
    private Uri selectedImageUri = null;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        edtBio = findViewById(R.id.edt_bio);
        btnSave = findViewById(R.id.btn_save);

        loadProfileData();

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadProfileData() {
        executorService.execute(() -> {
            try {
                Student student = DatabaseClient.getInstance(getApplicationContext())
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                if (student != null) {
                    runOnUiThread(() -> {
                        edtBio.setText(student.Bio != null ? student.Bio : "");

                        if (student.profilePictureUri != null && !student.profilePictureUri.isEmpty()) {
                            Glide.with(this)
                                    .load(Uri.parse(student.profilePictureUri))
                                    .placeholder(R.drawable.nav_profile_pic)
                                    .into(ivProfilePicture);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.nav_profile_pic)
                    .into(ivProfilePicture);
        }
    }

    private void saveProfileChanges() {
        String bio = edtBio.getText().toString().trim();

        executorService.execute(() -> {
            try {
                Student student = DatabaseClient.getInstance(getApplicationContext())
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                if (student != null) {
                    student.Bio = bio;
                    if (selectedImageUri != null) {
                        student.profilePictureUri = selectedImageUri.toString();
                    }

                    DatabaseClient.getInstance(getApplicationContext())
                            .getDatabase()
                            .studentDao()
                            .update(student);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
