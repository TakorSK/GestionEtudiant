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
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
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
    private Student currentStudent;
    private Uni currentUniversity;
    private int currentUniversityId;

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

        // Initialize views
        editPostTitle = view.findViewById(R.id.editPostTitle);
        editPostDescription = view.findViewById(R.id.editPostDescription);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);
        buttonAddImage = view.findViewById(R.id.buttonAddImage);
        selectedImagePreview = view.findViewById(R.id.selectedImagePreview);

        db = DatabaseClient.getInstance(requireContext()).getDatabase();

        // Safely get arguments
        Bundle args = getArguments();
        if (args != null) {
            try {
                currentUserType = LoginType.valueOf(args.getString("LOGIN_TYPE", LoginType.REGULAR_STUDENT.name()));
                currentUniversityId = args.getInt("UNIVERSITY_ID", -1);
            } catch (Exception e) {
                currentUserType = LoginType.REGULAR_STUDENT;
                currentUniversityId = -1;
            }
        } else {
            currentUserType = LoginType.REGULAR_STUDENT;
            currentUniversityId = -1;
        }

        loadUserData();

        buttonAddImage.setOnClickListener(v -> openImagePicker());
        buttonSubmitPost.setOnClickListener(v -> submitPost());

        return view;
    }

    private void loadUserData() {
        if (currentUniversityId == -1) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "University ID is missing", Toast.LENGTH_SHORT).show()
            );
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (currentUserType == LoginType.UNIVERSITY_ADMIN) {
                    currentUniversity = db.uniDao().getById(currentUniversityId);
                }
                else if (currentUserType == LoginType.STUDENT_ADMIN ||
                        currentUserType == LoginType.REGULAR_STUDENT) {
                    currentStudent = db.studentDao().getOnlineStudent();
                    if (currentStudent != null) {
                        currentUniversity = db.uniDao().getById(currentStudent.uniId);
                    }
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show());
            }
        });
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

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Format the post content with author information
                String authorInfo = getAuthorInformation();
                String fullDescription = description + "\n\n" + authorInfo;

                // Format the title based on user type
                String formattedTitle = formatTitle(title);

                // Get appropriate author ID
                int authorId = getAuthorId();

                Post post = new Post(formattedTitle, fullDescription, imageUriString, authorId);
                db.postDao().insert(post);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String formatTitle(String title) {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                return "[DEBUG ADMIN] " + title;
            case UNIVERSITY_ADMIN:
                return "[UNIVERSITY] " + title;
            case STUDENT_ADMIN:
                return "[ADMIN] " + title;
            default:
                return title;
        }
    }

    private String getAuthorInformation() {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                return "Posted by: ADMIN (System)";
            case UNIVERSITY_ADMIN:
                return "Posted by: " + (currentUniversity != null ? currentUniversity.name : "University") + " Administration";
            case STUDENT_ADMIN:
                return "Posted by: ADMIN (" + (currentStudent != null ? currentStudent.fullName : "Student") + ")";
            default:
                return "Posted by: " + (currentStudent != null ? currentStudent.fullName : "Student");
        }
    }

    private int getAuthorId() {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                return -1; // Special ID for debug admin
            case UNIVERSITY_ADMIN:
                return -currentUniversityId; // Negative university ID for university admins
            default:
                return currentStudent != null ? currentStudent.id : 0;
        }
    }

    private void resetForm() {
        requireActivity().runOnUiThread(() -> {
            editPostTitle.setText("");
            editPostDescription.setText("");
            selectedImagePreview.setVisibility(View.GONE);
            selectedImageUri = null;
        });
    }

    // Factory method to properly create the fragment with required arguments
    public static CreatePostFragment newInstance(LoginType loginType, int universityId) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString("LOGIN_TYPE", loginType != null ? loginType.name() : LoginType.REGULAR_STUDENT.name());
        args.putInt("UNIVERSITY_ID", universityId);
        fragment.setArguments(args);
        return fragment;
    }
}