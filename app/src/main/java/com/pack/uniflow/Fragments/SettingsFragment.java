package com.pack.uniflow.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.pack.uniflow.R;

public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.settings_switch_dark_mode);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean darkModeOn = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(darkModeOn);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.putString("last_fragment", "settings");
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            requireActivity().recreate();
        });


        return view;
    }
}
