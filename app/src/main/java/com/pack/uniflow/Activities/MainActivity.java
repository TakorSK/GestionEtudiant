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
    private LoginActivity.LoginType loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get login type from intent
        String loginTypeStr = getIntent().getStringExtra("LOGIN_TYPE");
        loginType = loginTypeStr != null ?
                LoginActivity.LoginType.valueOf(loginTypeStr) :
                LoginActivity.LoginType.REGULAR_STUDENT;

        initializeViews();
        setupProfileBasedOnLoginType();
        setupInitialFragment(savedInstanceState);
        setupBackButtonHandler();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

    private void setupProfileBasedOnLoginType() {
        switch (loginType) {
            case DEBUG_ADMIN:
                setupDebugAdminProfile();
                break;
            case UNIVERSITY_ADMIN:
                setupUniversityProfile();
                break;
            default:
                loadStudentProfile();
                break;
        }
    }

    private void setupDebugAdminProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Debug Admin");
            profileGroupTextView.setText("Developer Mode");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_admin).setVisible(true);
            menu.findItem(R.id.nav_post).setVisible(true);
        });
    }

    private void setupUniversityProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("University Admin");
            profileGroupTextView.setText("Administrator");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_admin).setVisible(true);
            menu.findItem(R.id.nav_post).setVisible(false);
        });
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
                        profileNameTextView.setText(
                                currentStudent.fullName.isEmpty() ?
                                        "Unknown Name" : currentStudent.fullName
                        );
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
            boolean shouldShowAdmin = currentStudent != null &&
                    (currentStudent.isAdmin || loginType == LoginActivity.LoginType.DEBUG_ADMIN);
            adminItem.setVisible(shouldShowAdmin);
        }
    }

    private void loadProfileImage(String imageUri) {
        runOnUiThread(() -> {
            try {
                if (imageUri != null && !imageUri.isEmpty()) {
                    Glide.with(this)
                            .load(Uri.parse(imageUri))
                            .placeholder(R.drawable.nav_profile_pic)
                            .error(R.drawable.nav_profile_pic)
                            .circleCrop()
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.nav_profile_pic);
                }
            } catch (Exception e) {
                profileImageView.setImageResource(R.drawable.nav_profile_pic);
            }
        });
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
            if (hasAdminAccess()) {
                selectedFragment = new AdminFragment();
            } else {
                showAccessDenied();
                return true;
            }
        } else if (id == R.id.nav_post) {
            if (loginType != LoginActivity.LoginType.UNIVERSITY_ADMIN) {
                selectedFragment = new CreatePostFragment();
            }
        } else if (id == R.id.nav_profile) {
            if (loginType != LoginActivity.LoginType.UNIVERSITY_ADMIN) {
                selectedFragment = new ProfileFragment();
            }
        } else if (id == R.id.nav_clubs) {
            selectedFragment = new ClubsFragment();
        } else if (id == R.id.nav_schedule) {
            if (loginType != LoginActivity.LoginType.UNIVERSITY_ADMIN) {
                selectedFragment = new ScheduleFragment();
            }
        } else if (id == R.id.nav_scores) {
            if (loginType != LoginActivity.LoginType.UNIVERSITY_ADMIN) {
                selectedFragment = new ScoresFragment();
            }
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (id == R.id.nav_logout) {
            handleLogout();
            return true;
        }

        if (selectedFragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading content", Toast.LENGTH_SHORT).show();
            }
        } else {
            showAccessDenied();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean hasAdminAccess() {
        return loginType == LoginActivity.LoginType.DEBUG_ADMIN ||
                loginType == LoginActivity.LoginType.UNIVERSITY_ADMIN ||
                (currentStudent != null && currentStudent.isAdmin);
    }

    private void showAccessDenied() {
        Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
    }

    private void handleLogout() {
        executorService.execute(() -> {
            try {
                if (loginType == LoginActivity.LoginType.REGULAR_STUDENT ||
                        loginType == LoginActivity.LoginType.STUDENT_ADMIN) {
                    DatabaseClient.getInstance(this)
                            .getDatabase()
                            .studentDao()
                            .setAllOffline();
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}

