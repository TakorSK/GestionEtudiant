package com.pack.uniflow.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profileUniTextView;
    private TextView profileSectionTextView;
    private ImageView statusIcon;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileNameTextView = view.findViewById(R.id.profile_Name);
        profileUniTextView = view.findViewById(R.id.profile_Uni);
        profileSectionTextView = view.findViewById(R.id.profile_Section);
        statusIcon = view.findViewById(R.id.status_icon);

        // Load student data
        loadStudentProfile();

        return view;
    }

    private void loadStudentProfile() {
        executorService.execute(() -> {
            try {
                // Get the latest student (assuming this is the logged-in user)
                Student loggedStudent = DatabaseClient.getInstance(requireContext())
                        .getDatabase()
                        .studentDao()
                        .getLatestStudent();

                if (loggedStudent != null) {
                    // Get university name
                    Uni university = DatabaseClient.getInstance(requireContext())
                            .getDatabase()
                            .uniDao()
                            .getById(loggedStudent.uniId);

                    requireActivity().runOnUiThread(() -> {
                        // Set student info
                        profileNameTextView.setText(loggedStudent.fullName);
                        profileUniTextView.setText(university != null ? university.name : "Unknown University");
                        profileSectionTextView.setText(loggedStudent.sectionId != null ? 
                            "Section " + loggedStudent.sectionId : "No Section");

                        // Set online status
                        statusIcon.setImageResource(loggedStudent.isOnline ? 
                            R.drawable.online_circle_icon : R.drawable.offline_circle_icon);
                    });
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    profileNameTextView.setText("Error loading profile");
                    profileUniTextView.setText("Please try again");
                    profileSectionTextView.setText("");
                    statusIcon.setImageResource(R.drawable.offline_circle_icon);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }
}