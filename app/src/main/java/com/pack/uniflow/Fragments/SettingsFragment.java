package com.pack.uniflow.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.pack.uniflow.R;
import com.pack.uniflow.Activities.ChangeProfileActivity;

public class SettingsFragment extends Fragment {

    private View btnChangeProfile; // Updated to View (LinearLayout)
    private Switch switchDarkMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnChangeProfile = view.findViewById(R.id.settings_btn_change_profile); // Change Profile Row
        switchDarkMode = view.findViewById(R.id.settings_switch_dark_mode);

        // Open ChangeProfileActivity
        btnChangeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeProfileActivity.class);
            startActivity(intent);
        });

        // Load current mode from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean darkModeOn = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(darkModeOn);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Save user preference
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
        });

        return view;
    }
}
