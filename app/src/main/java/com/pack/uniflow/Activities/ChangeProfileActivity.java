package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.pack.uniflow.R;
import com.bumptech.glide.Glide;

public class ChangeProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivProfilePicture;
    private EditText edtBio;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        edtBio = findViewById(R.id.edt_bio);
        btnSave = findViewById(R.id.btn_save);

        // Load the current profile picture and bio if available
        loadProfileData();

        // Profile picture click listener
        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        // Save button click listener
        btnSave.setOnClickListener(v -> {
            String bio = edtBio.getText().toString().trim();
            // Save the bio and profile picture to SharedPreferences
            saveBioToPreferences(bio);  // Save bio to preferences
            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        });
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Load profile picture URI from SharedPreferences
        String imageUriString = prefs.getString("profile_image_uri", null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);

            // Load image using Glide
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.nav_profile_pic)  // Placeholder if the image is loading
                    .into(ivProfilePicture);  // Set image to the ImageView
        }

        // Load bio (if saved)
        String bio = prefs.getString("bio", "");
        edtBio.setText(bio);
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
            Uri imageUri = data.getData();

            // Load the selected image using Glide
            Glide.with(this)
                    .load(imageUri)
                    .into(ivProfilePicture);

            // Save the image URI to SharedPreferences
            saveProfileImageUriToPreferences(imageUri);
        }
    }

    private void saveProfileImageUriToPreferences(Uri imageUri) {
        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profile_image_uri", imageUri.toString());
        editor.apply();

        // Log to confirm saving the URI
        Log.d("ChangeProfileActivity", "Profile image URI saved: " + imageUri.toString());
    }

    // Save bio to SharedPreferences
    private void saveBioToPreferences(String bio) {
        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bio", bio);
        editor.apply();
    }
}
