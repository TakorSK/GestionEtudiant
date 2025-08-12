package com.pack.uniflow.Fragments;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pack.uniflow.Activities.MainActivity;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, ageTextView,
            telephoneTextView, sectionTextView, universityTextView, bioTextView;
    private ImageView profileImageView, statusIconView;

    public ProfileFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views by IDs matching your layout
        nameTextView = root.findViewById(R.id.profile_name);
        emailTextView = root.findViewById(R.id.profile_email);
        ageTextView = root.findViewById(R.id.profile_age);
        telephoneTextView = root.findViewById(R.id.profile_telephone);
        sectionTextView = root.findViewById(R.id.profile_section);
        universityTextView = root.findViewById(R.id.profile_university);
        bioTextView = root.findViewById(R.id.profile_bio);
        profileImageView = root.findViewById(R.id.profile_image);
        statusIconView = root.findViewById(R.id.status_icon);

        // **Do NOT load profile here**
        // loadProfile();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateProfileUI();
    }

    public void updateProfileUI() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        Student student = activity.getCurrentStudent();
        Uni university = activity.getCurrentUniversity();

        if (student == null) {
            // No logged-in user case
            nameTextView.setText("No user logged in");
            emailTextView.setText("");
            ageTextView.setText("Age: -");
            telephoneTextView.setText("Tel: -");
            sectionTextView.setText("Section: -");
            universityTextView.setText("University: -");
            bioTextView.setText("No bio available.");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            statusIconView.setVisibility(View.GONE);
            return;
        }

        // Name & Email
        nameTextView.setText(nonEmpty(student.getFullName(), "Unknown"));
        emailTextView.setText(nonEmpty(student.getEmail(), "N/A"));

        // Age
        ageTextView.setText("Age: " + (student.getAge() > 0 ? student.getAge() : "-"));

        // Telephone
        telephoneTextView.setText("Tel: " + nonEmpty(student.getTelephone(), "-"));

        // Section (simple placeholder, replace with your logic)
        String sectionName = getSectionNameById(student.getSectionId());
        sectionTextView.setText("Section: " + sectionName);

        // University name
        universityTextView.setText("University: " +
                (university != null ? nonEmpty(university.getName(), "Unknown") : "Unknown"));

        // Bio
        String bio = student.getBio();
        bioTextView.setText(nonEmpty(bio, "No bio available."));

        // Profile picture with Glide
        String profileUri = student.getProfilePictureUri();
        if (profileUri != null && !profileUri.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(profileUri))
                    .placeholder(R.drawable.nav_profile_pic)
                    .error(R.drawable.nav_profile_pic)
                    .circleCrop()
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
        }

        // Online status icon visibility
        statusIconView.setVisibility(student.isOnline() ? View.VISIBLE : View.GONE);
    }

    // Helper to avoid empty/null strings
    private String nonEmpty(String value, String defaultVal) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultVal;
    }

    // Dummy method to get Section name from ID; replace with your actual data source
    private String getSectionNameById(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty()) return "-";
        // You can query a section map or database here
        return sectionId; // just return ID for now
    }
}
