package com.pack.uniflow.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.Fragments.AdminFragment;
import com.pack.uniflow.Fragments.ClubsFragment;
import com.pack.uniflow.Fragments.CreatePostFragment;
import com.pack.uniflow.Fragments.HomeFragment;
import com.pack.uniflow.Fragments.ProfileFragment;
import com.pack.uniflow.Fragments.ScheduleFragment;
import com.pack.uniflow.Fragments.ScoresFragment;
import com.pack.uniflow.Fragments.SettingsFragment;
import com.pack.uniflow.R;
import com.pack.uniflow.Section;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImageView;
    private TextView profileNameTextView, profileGroupTextView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        loadStudentProfile();
        setupInitialFragment(savedInstanceState);
        setupBackButtonHandler();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        updateNavigationHeaderViews();
    }

    private void updateNavigationHeaderViews() {
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.profile_image);
        profileNameTextView = headerView.findViewById(R.id.profile_name_text_view);
        profileGroupTextView = headerView.findViewById(R.id.profile_group);

        profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImageView.setClipToOutline(true);
    }

    private void loadStudentProfile() {
        executorService.execute(() -> {
            try {
                currentStudent = DatabaseClient.getInstance(this)
                        .getDatabase()
                        .studentDao()
                        .getOnlineStudent();

                if (currentStudent != null) {
                    Uni uni = DatabaseClient.getInstance(this)
                            .getDatabase()
                            .uniDao()
                            .getById(currentStudent.uniId);

                    Section section = currentStudent.sectionId != null ?
                            DatabaseClient.getInstance(this)
                                    .getDatabase()
                                    .sectionDao()
                                    .getById(currentStudent.sectionId) : null;

                    runOnUiThread(() -> {
                        // Use fullName directly
                        profileNameTextView.setText(currentStudent.fullName.isEmpty() ? "Unknown Name" : currentStudent.fullName);
                        profileGroupTextView.setText(buildProfileSubtitle(uni, section));
                        loadProfileImage(currentStudent.profilePictureUri);
                        updateAdminMenuItemVisibility();
                    });
                } else {
                    showDefaultProfile();
                }
            } catch (Exception e) {
                showErrorProfile();
            }
        });
    }

    private void updateAdminMenuItemVisibility() {
        Menu menu = navigationView.getMenu();
        MenuItem adminItem = menu.findItem(R.id.nav_admin);

        if (adminItem != null) {
            adminItem.setVisible(currentStudent != null && currentStudent.isAdmin);
        }
    }

    private void loadProfileImage(String imageUri) {
        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                Glide.with(this)
                        .load(Uri.parse(imageUri))
                        .placeholder(R.drawable.nav_profile_pic)
                        .error(R.drawable.nav_profile_pic)
                        .circleCrop()
                        .into(profileImageView);
            } catch (Exception e) {
                profileImageView.setImageResource(R.drawable.nav_profile_pic);
            }
        } else {
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
        }
    }

    private String buildProfileSubtitle(Uni uni, Section section) {
        StringBuilder subtitle = new StringBuilder();
        if (section != null) subtitle.append(section.name);
        if (uni != null) {
            if (subtitle.length() > 0) subtitle.append(" - ");
            subtitle.append(uni.name);
        }
        return subtitle.length() > 0 ? subtitle.toString() : "Unknown University";
    }

    private void setupInitialFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
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
                    finish();
                }
            }
        });
    }

    private void showDefaultProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("No student logged in");
            profileGroupTextView.setText("N/A");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
        });
    }

    private void showErrorProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Error");
            profileGroupTextView.setText("Loading failed");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_admin) {

            //TODO: PLEASE REVERT THIS BACK
            /*if (currentStudent != null && (currentStudent.isAdmin || currentStudent.fullName.equals("debug"))) {
                selectedFragment = new AdminFragment();
            } else {
                Toast.makeText(this, "Admin access denied", Toast.LENGTH_SHORT).show();
                return true;
            }*/

            //TODO: THEN REMOVE THIS LINE BELOW
            selectedFragment = new AdminFragment();

        } else if (id == R.id.nav_post) {
            selectedFragment = new CreatePostFragment();
        } else if (id == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (id == R.id.nav_clubs) {
            selectedFragment = new ClubsFragment();
        } else if (id == R.id.nav_schedule) {
            selectedFragment = new ScheduleFragment();
        } else if (id == R.id.nav_scores) {
            selectedFragment = new ScoresFragment();
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (id == R.id.nav_logout) {
            handleLogout();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        executorService.execute(() -> {
            try {
                DatabaseClient.getInstance(this)
                        .getDatabase()
                        .studentDao()
                        .setAllOffline();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
