package com.pack.uniflow.Activities;

import android.content.Intent;
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
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Student;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView profileNameTextView;  // TextView to display the profile name
    private TextView profileGroupTextView; // TextView to display the profile group

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

        // Set up the ActionBarDrawerToggle for opening and closing the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        profileNameTextView = navigationView.getHeaderView(0).findViewById(R.id.profile_name_text_view);
        profileGroupTextView = navigationView.getHeaderView(0).findViewById(R.id.profile_group);

        new Thread(() -> {
            // Assuming only one student is logged in (you can improve this later)
            Student loggedStudent = DatabaseClient.getInstance(getApplicationContext())
                    .getDatabase()
                    .studentDao()
                    .getLatestStudent(); // <-- new query we'll add

            runOnUiThread(() -> {
                if (loggedStudent != null) {
                    profileNameTextView.setText(loggedStudent.fullName);
                    profileGroupTextView.setText(loggedStudent.sectionId);
                } else {
                    profileNameTextView.setText("Guest"); // default if no student found
                    profileGroupTextView.setText("Unknown");
                }
            });
        }).start();

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
