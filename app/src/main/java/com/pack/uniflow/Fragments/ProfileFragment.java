package com.pack.uniflow.Fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pack.uniflow.R;
import com.bumptech.glide.Glide; // Import Glide

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profileUniTextView;
    private TextView profileSectionTextView;
    private TextView edtBioTextView;  // Add this TextView for bio
    private boolean isOnline = true;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("bio")) {
                updateBio();  // Refresh the bio when it's updated in SharedPreferences
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the TextViews after the view is inflated
        profileNameTextView = view.findViewById(R.id.profile_Name);
        profileUniTextView = view.findViewById(R.id.profile_Uni);
        profileSectionTextView = view.findViewById(R.id.profile_Section);
        edtBioTextView = view.findViewById(R.id.edt_bio);  // Initialize bio TextView

        // Set the Profile Image
        ImageView profileImageView = view.findViewById(R.id.profile_image);
        Uri imageUri = getProfileImageUriFromPreferences();

        if (imageUri != null) {
            // Using Glide to load the image
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.nav_profile_pic)  // Default image
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.nav_profile_pic); // Default image
        }

        // Initialize the ImageView for status
        ImageView statusIcon = view.findViewById(R.id.status_icon);

        if (isOnline) {
            statusIcon.setImageResource(R.drawable.online_circle_icon);
        } else {
            statusIcon.setImageResource(R.drawable.offline_circle_icon);
        }

        // Load bio from SharedPreferences when the fragment is created
        updateBio();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register the listener to listen for preference changes
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister the listener when the fragment is no longer visible
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    private void updateBio() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        String bio = prefs.getString("bio", "");
        edtBioTextView.setText(bio);  // Update the bio in the UI
    }

    private Uri getProfileImageUriFromPreferences() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPreferences", getContext().MODE_PRIVATE);
        String uriString = prefs.getString("profile_image_uri", null);

        if (uriString != null) {
            try {
                return Uri.parse(uriString); // Convert the saved string URI to Uri object
            } catch (Exception e) {
                e.printStackTrace(); // Log the error if URI parsing fails
            }
        }
        return null; // No image URI saved, return null
    }
}
