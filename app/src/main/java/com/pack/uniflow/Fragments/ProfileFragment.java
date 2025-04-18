package com.pack.uniflow.Fragments;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Section;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profileUniTextView;
    private TextView profileSectionTextView;
    private TextView edtBioTextView;
    private ImageView profileImageView, statusIcon;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileNameTextView = view.findViewById(R.id.profile_Name);
        profileUniTextView = view.findViewById(R.id.profile_Uni);
        profileSectionTextView = view.findViewById(R.id.profile_Section);
        edtBioTextView = view.findViewById(R.id.edt_bio);
        profileImageView = view.findViewById(R.id.profile_image);
        statusIcon = view.findViewById(R.id.status_icon);

        loadStudentProfile();

        return view;
    }

    private void loadStudentProfile() {
        executorService.execute(() -> {
            try {
                Student student = DatabaseClient.getInstance(getContext())
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                Uni uni = null;
                Section section = null;

                if (student != null) {
                    if (student.uniId != 0) {
                        uni = DatabaseClient.getInstance(getContext())
                                .getDatabase()
                                .uniDao()
                                .getById(student.uniId);
                    }
                    if (student.sectionId != null) {
                        section = DatabaseClient.getInstance(getContext())
                                .getDatabase()
                                .sectionDao()
                                .getById(student.sectionId);
                    }

                    Uni finalUni = uni;
                    Section finalSection = section;

                    requireActivity().runOnUiThread(() -> {
                        profileNameTextView.setText(student.fullName != null ? student.fullName : "Unknown");
                        profileUniTextView.setText(finalUni != null ? finalUni.name : "Unknown University");
                        profileSectionTextView.setText(finalSection != null ? finalSection.name : "Unknown Section");
                        edtBioTextView.setText(student.Bio != null ? student.Bio : "No bio available");

                        if (student.profilePictureUri != null && !student.profilePictureUri.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(Uri.parse(student.profilePictureUri))
                                    .placeholder(R.drawable.nav_profile_pic)
                                    .into(profileImageView);
                        } else {
                            profileImageView.setImageResource(R.drawable.nav_profile_pic);
                        }

                        statusIcon.setImageResource(student.isOnline ? R.drawable.online_circle_icon : R.drawable.offline_circle_icon);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        profileNameTextView.setText("Not logged in");
                        profileUniTextView.setText("N/A");
                        profileSectionTextView.setText("N/A");
                        edtBioTextView.setText("N/A");
                        profileImageView.setImageResource(R.drawable.nav_profile_pic);
                        statusIcon.setImageResource(R.drawable.offline_circle_icon);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    profileNameTextView.setText("Error loading data");
                    profileUniTextView.setText("Error");
                    profileSectionTextView.setText("Error");
                    edtBioTextView.setText("Error");
                    profileImageView.setImageResource(R.drawable.nav_profile_pic);
                    statusIcon.setImageResource(R.drawable.offline_circle_icon);
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
