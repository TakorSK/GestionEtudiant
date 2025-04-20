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
import com.pack.uniflow.Fragments.*;
import com.pack.uniflow.R;
import com.pack.uniflow.Section;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.Activities.LoginActivity.LoginType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImageView;
    private TextView profileNameTextView, profileGroupTextView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Student currentStudent;
    private Uni currentUniversity;
    private LoginType loginType;
    private int currentUniversityId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Safely get login type and university ID
        try {
            String loginTypeStr = getIntent().getStringExtra("LOGIN_TYPE");
            loginType = loginTypeStr != null ?
                    LoginType.valueOf(loginTypeStr) :
                    LoginType.REGULAR_STUDENT;

            currentUniversityId = getIntent().getIntExtra("UNIVERSITY_ID", -1);
        } catch (Exception e) {
            loginType = LoginType.REGULAR_STUDENT;
            currentUniversityId = -1;
        }

        initializeViews();
        loadUserData();
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

    private void loadUserData() {
        executorService.execute(() -> {
            try {
                UniflowDB db = DatabaseClient.getInstance(this).getDatabase();

                if (loginType == LoginType.UNIVERSITY_ADMIN) {
                    currentUniversity = db.uniDao().getById(currentUniversityId);
                    updateUniversityProfile();
                }
                else if (loginType == LoginType.DEBUG_ADMIN) {
                    updateDebugAdminProfile();
                }
                else {
                    currentStudent = db.studentDao().getOnlineStudent();
                    if (currentStudent != null) {
                        currentUniversity = db.uniDao().getById(currentStudent.uniId);
                        currentUniversityId = currentStudent.uniId; // Update university ID from student
                        updateStudentProfile();
                    } else {
                        showDefaultProfile();
                    }
                }
            } catch (Exception e) {
                runOnUiThread(this::showErrorProfile);
            }
        });
    }

    private void updateUniversityProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("University Admin");
            profileGroupTextView.setText(currentUniversity != null ?
                    currentUniversity.name : "Administrator");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            updateMenuVisibility(false, false);
        });
    }

    private void updateDebugAdminProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Debug Admin");
            profileGroupTextView.setText("Developer Mode");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            updateMenuVisibility(true, false);
        });
    }

    private void updateStudentProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText(currentStudent.fullName.isEmpty() ?
                    "Unknown Name" : currentStudent.fullName);

            String subtitle = currentUniversity != null ? currentUniversity.name : "Unknown University";
            profileGroupTextView.setText(subtitle);

            loadProfileImage(currentStudent.profilePictureUri);
            updateMenuVisibility(
                    currentStudent.isAdmin || loginType == LoginType.DEBUG_ADMIN,
                    true
            );
        });
    }

    private void updateMenuVisibility(boolean showAdminItems, boolean showStudentItems) {
        Menu menu = navigationView.getMenu();
        menu.setGroupVisible(R.id.group_common, true);
        menu.setGroupVisible(R.id.group_admin, showAdminItems);
        menu.setGroupVisible(R.id.group_student, showStudentItems);

        if (loginType == LoginType.UNIVERSITY_ADMIN) {
            menu.findItem(R.id.nav_post).setVisible(true);
        }
    }

    private void updateNavigationHeaderViews() {
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.profile_image);
        profileNameTextView = headerView.findViewById(R.id.profile_name_text_view);
        profileGroupTextView = headerView.findViewById(R.id.profile_group);
        profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImageView.setClipToOutline(true);
    }

    private void loadProfileImage(String imageUri) {
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
    }

    private void setupInitialFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // Create HomeFragment with proper arguments
            HomeFragment homeFragment = HomeFragment.newInstance(loginType, currentUniversityId);
            navigateToFragment(homeFragment, R.id.nav_home);
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
            updateMenuVisibility(false, false);
        });
    }

    private void showErrorProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Error");
            profileGroupTextView.setText("Loading failed");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            updateMenuVisibility(false, false);
            Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = HomeFragment.newInstance(loginType, currentUniversityId);
        }
        else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        }
        else if (id == R.id.nav_logout) {
            handleLogout();
            return true;
        }
        else if (id == R.id.nav_post && hasPostingAccess()) {
            fragment = CreatePostFragment.newInstance(loginType, currentUniversityId);
        }
        else if (id == R.id.nav_admin && hasAdminAccess()) {
            fragment = new AdminFragment();
        }
        else if (hasStudentAccess()) {
            if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            else if (id == R.id.nav_clubs) {
                fragment = new ClubsFragment();
            }
            else if (id == R.id.nav_schedule) {
                fragment = new ScheduleFragment();
            }
            else if (id == R.id.nav_scores) {
                fragment = new ScoresFragment();
            }
        }

        if (fragment != null) {
            navigateToFragment(fragment, id);
        } else {
            showAccessDenied();
        }

        return true;
    }

    private void navigateToFragment(Fragment fragment, int menuItemId) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            navigationView.setCheckedItem(menuItemId);
            drawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading content", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasAdminAccess() {
        return loginType == LoginType.DEBUG_ADMIN ||
                (currentStudent != null && currentStudent.isAdmin);
    }

    private boolean hasPostingAccess() {
        return loginType == LoginType.UNIVERSITY_ADMIN ||
                loginType == LoginType.DEBUG_ADMIN ||
                (currentStudent != null && currentStudent.isAdmin);
    }

    private boolean hasStudentAccess() {
        return loginType == LoginType.REGULAR_STUDENT ||
                loginType == LoginType.STUDENT_ADMIN;
    }

    private void showAccessDenied() {
        Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
    }

    private void handleLogout() {
        executorService.execute(() -> {
            try {
                UniflowDB db = DatabaseClient.getInstance(this).getDatabase();

                if (loginType == LoginType.REGULAR_STUDENT ||
                        loginType == LoginType.STUDENT_ADMIN) {
                    db.studentDao().setAllOffline();
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