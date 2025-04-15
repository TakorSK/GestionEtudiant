package com.pack.uniflow.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pack.uniflow.R;

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

        // Profile picture click listener
        ivProfilePicture.setOnClickListener(v -> {
            openImagePicker();
            Toast.makeText(this, "Select a profile picture", Toast.LENGTH_SHORT).show();
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            String bio = edtBio.getText().toString().trim();

            // TODO: Save the bio and profile picture to your DB/server/etc.
            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
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
            Uri imageUri = data.getData();
            ivProfilePicture.setImageURI(imageUri);
            // Optionally store the URI or upload the image
        }
    }
}
