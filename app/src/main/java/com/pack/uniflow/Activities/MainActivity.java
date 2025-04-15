package com.pack.uniflow.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private TextView profileNameTextView;
    private TextView profileGroupTextView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        initializeToolbar();
        initializeNavigationDrawer();
        loadStudentProfile();
        setupInitialFragment(savedInstanceState);
        setupBackButtonHandler();
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void initializeNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        profileNameTextView = headerView.findViewById(R.id.profile_name_text_view);
        profileGroupTextView = headerView.findViewById(R.id.profile_group);
    }

    private void loadStudentProfile() {
        executorService.execute(() -> {
            try {
                Student loggedStudent = DatabaseClient.getInstance(getApplicationContext())
                        .getDatabase()
                        .studentDao()
                        .getLatestStudent();

                runOnUiThread(() -> {
                    if (loggedStudent != null) {
                        profileNameTextView.setText(loggedStudent.fullName);
                        String sectionText = loggedStudent.sectionId != null ?
                                "Section " + loggedStudent.sectionId : "No Section";
                        profileGroupTextView.setText(sectionText);
                    } else {
                        profileNameTextView.setText("Guest");
                        profileGroupTextView.setText("Unknown");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading student profile", e);
                runOnUiThread(() -> {
                    profileNameTextView.setText("Error");
                    profileGroupTextView.setText("Try again");
                });
            }
        });
    }

    private void setupInitialFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Optional: Add confirmation dialog before exit
                    // finishAffinity(); // Uncomment if you want to close app on back press
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (itemId == R.id.nav_clubs) {
            selectedFragment = new ClubsFragment();
        } else if (itemId == R.id.nav_schedule) {
            selectedFragment = new ScheduleFragment();
        } else if (itemId == R.id.nav_scores) {
            selectedFragment = new ScoresFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_logout) {
            handleLogout();
            return true;
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void handleLogout() {
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
}