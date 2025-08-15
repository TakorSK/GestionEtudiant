package com.pack.uniflow.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;
import com.pack.uniflow.Models.Post;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.TagConstants;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatePostFragment extends Fragment {

    private EditText editPostTitle, editPostDescription;
    private Button buttonSubmitPost, buttonAddImage, buttonSelectTags;
    private ImageView selectedImagePreview;
    private TextView textSelectedTags;
    private Uri selectedImageUri;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference postsRef    = database.getReference("posts");
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference unisRef     = database.getReference("universities");

    private LoginType currentUserType = LoginType.REGULAR_STUDENT;
    private Student currentStudent;
    private Uni currentUniversity;
    private String currentUniversityId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    selectedImagePreview.setImageURI(selectedImageUri);
                    selectedImagePreview.setVisibility(View.VISIBLE);
                }
            });

    // Tag selection using TagConstants
    private final String[] availableTags = {
            TagConstants.DEBUG_ADMINS,
            TagConstants.THIS_UNI,
            TagConstants.THIS_CLUB,
            TagConstants.GlobalAnnouncement // renamed from "all"
    };

    private String selectedTag = TagConstants.DEBUG_ADMINS;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        bindViews(view);
        extractArguments();
        loadUserData();
        setupListeners();
        return view;
    }

    private void bindViews(View root) {
        editPostTitle        = root.findViewById(R.id.editPostTitle);
        editPostDescription  = root.findViewById(R.id.editPostDescription);
        buttonSubmitPost     = root.findViewById(R.id.buttonSubmitPost);
        buttonAddImage       = root.findViewById(R.id.buttonAddImage);
        selectedImagePreview = root.findViewById(R.id.selectedImagePreview);
        buttonSelectTags     = root.findViewById(R.id.buttonSelectTags);
        textSelectedTags     = root.findViewById(R.id.textSelectedTags);
    }

    private void extractArguments() {
        Bundle args = getArguments();
        if (args == null) return;
        try {
            currentUserType     = LoginType.valueOf(args.getString("LOGIN_TYPE", LoginType.REGULAR_STUDENT.name()));
            currentUniversityId = args.getString("UNIVERSITY_ID", null);
        } catch (Exception ignored) {}
    }

    private void setDefaultTag() {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                selectedTag = TagConstants.DEBUG_ADMINS;
                break;
            case UNIVERSITY_ADMIN:
            case STUDENT_ADMIN:
                selectedTag = TagConstants.UNI_ADMINS;
                break;
            case REGULAR_STUDENT:
                selectedTag = TagConstants.THIS_UNI;
                break;
            default:
                selectedTag = TagConstants.GlobalAnnouncement; // fallback or unknown type
        }
        updateSelectedTagsView(); // Update the UI
    }

    private void loadUserData() {
        if (currentUserType == LoginType.UNIVERSITY_ADMIN) {
            if (currentUniversityId != null) {
                unisRef.child(currentUniversityId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUniversity = snapshot.getValue(Uni.class);
                        if (currentUniversity != null) {
                            try {
                                currentUniversity.setId(Integer.parseInt(snapshot.getKey()));
                            } catch (NumberFormatException e) {
                                currentUniversity.setId(-1); // fallback
                            }
                            setDefaultTag(); // Set tag based on login type
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showToast("Error loading university");
                    }
                });
            }
        } else {
            // Load the currently online student — assumes only one online student for demo purposes
            studentsRef.orderByChild("isOnline").equalTo(true).limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Student student = snap.getValue(Student.class);
                                if (student != null) {
                                    student.setId(snap.getKey());
                                    currentStudent = student;
                                    currentUniversityId = student.getUniId();
                                    if (currentUniversityId != null) {
                                        unisRef.child(currentUniversityId).addListenerForSingleValueEvent(new UniListener());
                                    }
                                    break;
                                }
                            }
                            setDefaultTag(); // Set tag based on login type
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            showToast("Error loading user data");
                        }
                    });
        }
    }

    private void setupListeners() {
        buttonAddImage.setOnClickListener(v -> openImagePicker());
        buttonSubmitPost.setOnClickListener(v -> submitPost());
        buttonSelectTags.setOnClickListener(v -> showTagSelectionDialog());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showTagSelectionDialog() {
        int preselected = -1;
        if (selectedTag != null) {
            for (int i = 0; i < availableTags.length; i++) {
                if (availableTags[i].equals(selectedTag)) {
                    preselected = i;
                    break;
                }
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Tag")
                .setSingleChoiceItems(availableTags, preselected, (dialog, which) -> selectedTag = availableTags[which])
                .setPositiveButton("OK", (dialog, which) -> updateSelectedTagsView())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateSelectedTagsView() {
        if (selectedTag == null) {
            textSelectedTags.setText("Tag: None");
        } else {
            textSelectedTags.setText("Tag: " + selectedTag);
        }
    }

    private void submitPost() {
        String title       = editPostTitle.getText().toString().trim();
        String description = editPostDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editPostTitle.setError("Title is required");
            return;
        }

        if (selectedTag == null) {
            showToast("Please select a tag");
            return;
        }

        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : null;
        String authorName     = getAuthorName();
        String profileImage   = getAuthorProfileImage();

        Post post = new Post(
                title,
                description,
                imageUriString,
                getAuthorId(),
                authorName,
                profileImage,
                Collections.singletonList(selectedTag) // Only one tag
        );

        String newId = postsRef.push().getKey();
        if (newId != null) {
            post.setId(newId);
            postsRef.child(newId).setValue(post)
                    .addOnSuccessListener(unused -> {
                        showToast("Post created successfully!");
                        resetForm();
                    })
                    .addOnFailureListener(e -> showToast("Failed to create post"));
        } else {
            showToast("Failed to generate post ID");
        }
    }

    private String getAuthorName() {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                return "ADMIN (System)";
            case UNIVERSITY_ADMIN:
                return currentUniversity != null ? currentUniversity.getName() + " Administration" : "University";
            case STUDENT_ADMIN:
                return currentStudent != null ? "ADMIN (" + currentStudent.getFullName() + ")" : "Student Admin";
            default:
                return currentStudent != null ? currentStudent.getFullName() : "Student";
        }
    }

    private String getAuthorId() {
        switch (currentUserType) {
            case DEBUG_ADMIN:
                return "debug_admin";
            case UNIVERSITY_ADMIN:
                return currentUniversityId != null ? "uni_" + currentUniversityId : "uni_unknown";
            default:
                return (currentStudent != null && currentStudent.getId() != null) ? currentStudent.getId() : "unknown";
        }
    }

    private String getAuthorProfileImage() {
        if (currentUserType == LoginType.UNIVERSITY_ADMIN) return null;
        return currentStudent != null ? currentStudent.getProfilePictureUri() : null;
    }

    private void resetForm() {
        editPostTitle.setText("");
        editPostDescription.setText("");
        selectedImagePreview.setVisibility(View.GONE);
        selectedImageUri = null;
        selectedTag = null;
        updateSelectedTagsView();
    }

    private void showToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private class UniListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            currentUniversity = snapshot.getValue(Uni.class);
            if (currentUniversity != null) {
                try {
                    currentUniversity.setId(Integer.parseInt(snapshot.getKey()));
                } catch (NumberFormatException e) {
                    currentUniversity.setId(-1);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            showToast("Error loading university");
        }
    }

    public static CreatePostFragment newInstance(LoginType loginType, String universityId) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString("LOGIN_TYPE", loginType != null ? loginType.name() : LoginType.REGULAR_STUDENT.name());
        args.putString("UNIVERSITY_ID", universityId);
        fragment.setArguments(args);
        return fragment;
    }
}
