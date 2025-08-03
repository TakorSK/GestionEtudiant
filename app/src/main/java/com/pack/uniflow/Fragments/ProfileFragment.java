package com.pack.uniflow.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Section;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
import com.pack.uniflow.R;

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profileUniTextView;
    private TextView profileSectionTextView;
    private TextView edtBioTextView;
    private ImageView profileImageView, statusIcon;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference unisRef = database.getReference("universities");
    private final DatabaseReference sectionsRef = database.getReference("sections");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileNameTextView = view.findViewById(R.id.profile_Name);
        profileUniTextView = view.findViewById(R.id.profile_Uni);
        profileSectionTextView = view.findViewById(R.id.profile_Section);
        edtBioTextView = view.findViewById(R.id.edt_bio);
        profileImageView = view.findViewById(R.id.profile_image);
        statusIcon = view.findViewById(R.id.status_icon);

        loadOnlineStudent();
        return view;
    }

    private void loadOnlineStudent() {
        studentsRef.orderByChild("isOnline").equalTo(true).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Student student = snap.getValue(Student.class);
                                if (student == null) continue;
                                student.setId(snap.getKey());
                                bindStudentData(student);
                                break;
                            }
                        } else {
                            displayOfflineState("Not logged in");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        displayOfflineState("Error loading profile");
                    }
                });
    }

    private void bindStudentData(Student student) {
        profileNameTextView.setText(student.getFullName() != null ? student.getFullName() : "Unknown");
        edtBioTextView.setText(student.getBio() != null ? student.getBio() : "No bio available");
        statusIcon.setImageResource(student.isOnline() ? R.drawable.online_circle_icon : R.drawable.offline_circle_icon);

        if (student.getProfilePictureUri() != null && !student.getProfilePictureUri().isEmpty()) {
            Glide.with(requireContext())
                    .load(Uri.parse(student.getProfilePictureUri()))
                    .placeholder(R.drawable.nav_profile_pic)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
        }

        loadUniversityName(student.getUniId());
        loadSectionName(student.getSectionId());
    }

    private void loadUniversityName(String uniId) {
        if (uniId == null) {
            profileUniTextView.setText("Unknown University");
            return;
        }
        unisRef.child(uniId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Uni uni = snapshot.getValue(Uni.class);
                profileUniTextView.setText(uni != null ? uni.getName() : "Unknown University");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                profileUniTextView.setText("Error");
            }
        });
    }

    private void loadSectionName(String sectionId) {
        if (sectionId == null) {
            profileSectionTextView.setText("Unknown Section");
            return;
        }
        sectionsRef.child(sectionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Section section = snapshot.getValue(Section.class);
                profileSectionTextView.setText(section != null ? section.getName() : "Unknown Section");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                profileSectionTextView.setText("Error");
            }
        });
    }

    private void displayOfflineState(String message) {
        profileNameTextView.setText(message);
        profileUniTextView.setText("N/A");
        profileSectionTextView.setText("N/A");
        edtBioTextView.setText("N/A");
        profileImageView.setImageResource(R.drawable.nav_profile_pic);
        statusIcon.setImageResource(R.drawable.offline_circle_icon);
    }
}