package com.pack.uniflow.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pack.uniflow.R;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.concurrent.Executors;

public class CreatePostFragment extends Fragment {

    private EditText editPostTitle, editPostDescription;
    private Button buttonSubmitPost, buttonAddImage;
    private ImageView selectedImagePreview;
    private Uri selectedImageUri;
    private UniflowDB db;
    private LoginType currentUserType;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    selectedImagePreview.setImageURI(selectedImageUri);
                    selectedImagePreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        // Initialize the views
        editPostTitle = view.findViewById(R.id.editPostTitle);
        editPostDescription = view.findViewById(R.id.editPostDescription);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);
        buttonAddImage = view.findViewById(R.id.buttonAddImage);
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);

        db = DatabaseClient.getInstance(requireContext()).getDatabase();

        // Get the current user type (you'll need to pass this to the fragment or get it from shared prefs)
        // For this example, I'm assuming you have a way to get the current user type
        currentUserType = getCurrentUserType(); // Implement this method based on your app's auth system

        // Handle image picking
        buttonAddImage.setOnClickListener(v -> openImagePicker());

        // Handle submit post
        buttonSubmitPost.setOnClickListener(v -> submitPost());

        return view;
    }

    private LoginType getCurrentUserType() {
        // Implement this based on how you store user session in your app
        // This could come from SharedPreferences, a SessionManager, or passed as arguments
        // For now, returning DEBUG_ADMIN as example
        return LoginType.DEBUG_ADMIN;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void submitPost() {
        String title = editPostTitle.getText().toString().trim();
        String description = editPostDescription.getText().toString().trim();
        String imageUriString = (selectedImageUri != null) ? selectedImageUri.toString() : null;

        if (title.isEmpty()) {
            editPostTitle.setError("Title is required");
            return;
        }

        // Handle different author types
        int authorId = (currentUserType == LoginType.DEBUG_ADMIN) ? -1 : getCurrentUserId();

        // Create post with existing constructor
        Post post = new Post(title, description, imageUriString, authorId);

        // For debug admin, we can add a prefix to the title or description
        if (currentUserType == LoginType.DEBUG_ADMIN) {
            String debugPrefix = "[DEBUG ADMIN POST] ";
            post.setTitle(debugPrefix + title);
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            db.postDao().insert(post);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Post created!", Toast.LENGTH_SHORT).show();
                editPostTitle.setText("");
                editPostDescription.setText("");
                selectedImagePreview.setVisibility(View.GONE);
                selectedImageUri = null;
            });
        });
    }

    private int getCurrentUserId() {
        // Implement this to get the actual user ID for non-debug users
        // This would come from your authentication system
        return 1; // Placeholder - replace with actual implementation
    }
}