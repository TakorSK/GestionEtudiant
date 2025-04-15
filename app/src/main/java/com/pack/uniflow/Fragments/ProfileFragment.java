package com.pack.uniflow.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profileUniTextView;
    private TextView profileSectionTextView;

    private boolean isOnline = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the TextViews after the view is inflated
        profileNameTextView = view.findViewById(R.id.profile_Name);
        profileUniTextView = view.findViewById(R.id.profile_Uni);
        profileSectionTextView = view.findViewById(R.id.profile_Section);

        // TODO: Replace the "loggedStudent" with the actual logged student from the database.
        /* if (loggedStudent != null) {
            profileNameTextView.setText("Name: " + loggedStudent.fullName);
            profileUniTextView.setText("University: " + loggedStudent.UniName);
            profileSectionTextView.setText("Section: " + loggedStudent.SectionId);
        } */

        // Initialize the ImageView for status
        ImageView statusIcon = view.findViewById(R.id.status_icon);

        // TODO: Replace "isOnline" with the actual DB online indicator
        if (isOnline) {
            statusIcon.setImageResource(R.drawable.online_circle_icon);
        } else {
            statusIcon.setImageResource(R.drawable.offline_circle_icon);
        }

        return view;
    }
}
