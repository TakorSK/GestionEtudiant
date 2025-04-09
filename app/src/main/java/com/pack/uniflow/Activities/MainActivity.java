package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.pack.uniflow.Fragments.ClubsFragment;
import com.pack.uniflow.Fragments.HomeFragment;
import com.pack.uniflow.Fragments.ProfileFragment;
import com.pack.uniflow.Fragments.ScheduleFragment;
import com.pack.uniflow.Fragments.ScoresFragment;
import com.pack.uniflow.Fragments.SettingsFragment;
import com.pack.uniflow.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView profileNameTextView;  // TextView to display the profile name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);  // Hide the title

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get reference to the profile name TextView in the navigation header
        profileNameTextView = navigationView.getHeaderView(0).findViewById(R.id.profile_name_text_view);  // Make sure this ID matches in your layout

        // Retrieve the user's name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(SignupActivity.PREFS_NAME, MODE_PRIVATE);
        String userName = prefs.getString("NAME", "Guest");  // Default to "Guest" if no name is found

        // Set the user's name in the profile name TextView
        profileNameTextView.setText(userName);

        // Set up the ActionBarDrawerToggle for opening and closing the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Check if this is the first time opening the app or if we need to restore fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Handle back button press using OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Optionally, do nothing or add your custom back logic
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.nav_clubs) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClubsFragment()).commit();
        } else if (itemId == R.id.nav_schedule) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).commit();
        } else if (itemId == R.id.nav_scores) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScoresFragment()).commit();
        } else if (itemId == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            // Clear the login state in SharedPreferences
            SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LoginActivity.IS_LOGGED_IN, false);  // Set login state to false (logged out)
            editor.apply();

            // Show a Toast message to confirm logout
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Close MainActivity so the user can't go back to it
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Override onBackPressed to prevent returning to LoginActivity
    @Override
    public void onBackPressed() {
        // Do nothing when the back button is pressed
        // You could show a confirmation dialog if you want
    }
}
