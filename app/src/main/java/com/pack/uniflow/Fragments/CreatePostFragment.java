package com.pack.uniflow.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pack.uniflow.R;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.UniflowDB;

import java.util.concurrent.Executors;

public class CreatePostFragment extends Fragment {

    private EditText editPostTitle, editPostDescription;
    private Button buttonSubmitPost;
    private ImageView selectedImagePreview; // If you're showing the image preview
    private Uri selectedImageUri; // If you're handling image picking
    private UniflowDB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        // Initialize the views
        editPostTitle = view.findViewById(R.id.editPostTitle);
        editPostDescription = view.findViewById(R.id.editPostDescription);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);

        // Get the Room DB instance from DatabaseClient
        db = DatabaseClient.getInstance(requireContext()).getDatabase();

        // Handle submit button click
        buttonSubmitPost.setOnClickListener(v -> {
            String title = editPostTitle.getText().toString().trim();
            String description = editPostDescription.getText().toString().trim();
            String imageUri = (selectedImageUri != null) ? selectedImageUri.toString() : null;

            // If the title is empty, show an error
            if (title.isEmpty()) {
                editPostTitle.setError("Title is required");
                return;
            }

            // Create a new Post object
            Post post = new Post(title, description, imageUri, 1); // Replace 1 with actual author ID

            // Insert the post in a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                db.postDao().insert(post); // Insert the post

                // Update the UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Post created!", Toast.LENGTH_SHORT).show();

                    // Optionally reset fields or navigate away after posting
                    editPostTitle.setText("");
                    editPostDescription.setText("");
                });
            });
        });

        return view;
    }
}
