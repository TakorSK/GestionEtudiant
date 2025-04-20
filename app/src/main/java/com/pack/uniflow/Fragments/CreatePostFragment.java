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

import androidx.fragment.app.Fragment;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePostFragment extends Fragment {

    private EditText editTitle, editDescription;
    private ImageView imageViewPostImage;
    private Button buttonSubmitPost;
    private Uri selectedImageUri;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        editTitle = view.findViewById(R.id.editTitle);
        editDescription = view.findViewById(R.id.editDescription);
        imageViewPostImage = view.findViewById(R.id.imageViewPostImage);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);

        buttonSubmitPost.setOnClickListener(v -> submitPost());
        imageViewPostImage.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        // Image picker implementation would go here
    }

    private void submitPost() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Title and Description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;
        Post newPost = new Post(title, description, imageUri, 1); // Assuming '1' as authorId

        executorService.execute(() -> {
            try {
                // Insert post and get the row ID
                long postId = DatabaseClient.getInstance(getContext())
                        .getDatabase()
                        .postDao()
                        .insert(newPost);

                requireActivity().runOnUiThread(() -> {
                    if (postId > 0) {  // Check if insertion was successful
                        Toast.makeText(getContext(), "Post Created Successfully!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(getContext(), "Error Creating Post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}