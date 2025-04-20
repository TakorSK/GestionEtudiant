package com.pack.uniflow.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 2;

    private ImageView ivProfilePicture;
    private EditText edtBio;
    private Button btnSave;
    private Uri selectedImageUri;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        edtBio = findViewById(R.id.edt_bio);
        btnSave = findViewById(R.id.btn_save);

        loadProfileData();

        ivProfilePicture.setOnClickListener(v -> checkPermissionsAndOpenImagePicker());
        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadProfileData() {
        executorService.execute(() -> {
            try {
                Student student = DatabaseClient.getInstance(this)
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                runOnUiThread(() -> {
                    if (student != null) {
                        edtBio.setText(student.Bio != null ? student.Bio : "");

                        if (student.profilePictureUri != null && !student.profilePictureUri.isEmpty()) {
                            loadImageWithGlide(Uri.parse(student.profilePictureUri), ivProfilePicture);
                        }
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void checkPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission needed to select photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                loadImageWithGlide(selectedImageUri, ivProfilePicture);
            }
        }
    }

    private void saveProfileChanges() {
        String bio = edtBio.getText().toString().trim();

        executorService.execute(() -> {
            try {
                Student student = DatabaseClient.getInstance(this)
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                if (student != null) {
                    student.Bio = bio;

                    if (selectedImageUri != null) {
                        // Save image to internal storage and get new URI
                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        student.profilePictureUri = savedImagePath;
                    }

                    DatabaseClient.getInstance(this)
                            .getDatabase()
                            .studentDao()
                            .update(student);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String imageFileName = "profile_" + timeStamp + ".jpg";
            File storageDir = getFilesDir();
            File imageFile = new File(storageDir, imageFileName);

            try (InputStream in = getContentResolver().openInputStream(imageUri);
                 OutputStream out = openFileOutput(imageFileName, MODE_PRIVATE)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return Uri.fromFile(imageFile).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void loadImageWithGlide(Uri uri, ImageView imageView) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.nav_profile_pic)
                .error(R.drawable.nav_profile_pic)
                .circleCrop()
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}