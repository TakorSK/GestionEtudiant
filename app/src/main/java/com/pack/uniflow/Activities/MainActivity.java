package com.pack.uniflow.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import android.os.Handler;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.Fragments.AdminFragment;
import com.pack.uniflow.Fragments.ClubsFragment;
import com.pack.uniflow.Fragments.CreatePostFragment;
import com.pack.uniflow.Fragments.HomeFragment;
import com.pack.uniflow.Fragments.MessagesFragment;
import com.pack.uniflow.Fragments.ProfileFragment;
import com.pack.uniflow.Fragments.ScheduleFragment;
import com.pack.uniflow.Fragments.SettingsFragment;
import com.pack.uniflow.Fragments.ScoresFragment;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.R;
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
    private String currentUniversityId;

    // Firebase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference studentsRef = database.getReference("students");
    private final DatabaseReference unisRef     = database.getReference("universities");

    // Network connectivity checking
    private Handler connectivityHandler = new Handler();
    private Runnable connectivityRunnable;
    private AlertDialog noInternetDialog;
    private boolean isDialogVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Read saved dark mode preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkModeOn = prefs.getBoolean("dark_mode", false);

        //Redirect if not logged in
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // User not logged in → go to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Apply mode before calling super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
                darkModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try { database.setPersistenceEnabled(true); } catch (Exception ignored) {}

        String loginTypeStr = getIntent().getStringExtra("LOGIN_TYPE");
        loginType           = loginTypeStr != null ? LoginType.valueOf(loginTypeStr) : LoginType.REGULAR_STUDENT;
        currentUniversityId = getIntent().getStringExtra("UNIVERSITY_ID");

        initializeViews();
        loadUserData();
        setupInitialFragment();
        setupBackButtonHandler();

        // Setup periodic connectivity check runnable
        connectivityRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isNetworkAvailable(MainActivity.this)) {
                    runOnUiThread(() -> showNoInternetDialog());
                } else {
                    if (noInternetDialog != null && noInternetDialog.isShowing()) {
                        noInternetDialog.dismiss();
                    }
                }
                connectivityHandler.postDelayed(this, 5000);
            }
        };

        connectivityHandler.post(connectivityRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityHandler.removeCallbacks(connectivityRunnable);
    }

    private void showNoInternetDialog() {
        if (isDialogVisible) return; // Already visible, don't show again

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedAlertDialog);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        noInternetDialog = builder.create();
        noInternetDialog.show();

        // Clear window background to transparent to remove weird corners
        if (noInternetDialog.getWindow() != null) {
            noInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        isDialogVisible = true;

        noInternetDialog.setOnDismissListener(dialog -> isDialogVisible = false);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    // -------------------- UI -------------------------------------------------
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);
        profileImageView     = header.findViewById(R.id.profile_image);
        profileNameTextView  = header.findViewById(R.id.profile_name_text_view);
        profileGroupTextView = header.findViewById(R.id.profile_group);
        profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImageView.setClipToOutline(true);
    }

    // -------------------- Data ----------------------------------------------
    private void loadUserData() {
        if (loginType == LoginType.UNIVERSITY_ADMIN) {
            loadUniversityAdminData();
        } else if (loginType == LoginType.DEBUG_ADMIN) {
            updateDebugAdminProfile();
        } else {
            loadStudentData();
        }
    }

    private void loadUniversityAdminData() {
        if (currentUniversityId == null) { showErrorProfile(); return; }
        unisRef.child(currentUniversityId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUniversity = snapshot.getValue(Uni.class);
                if (currentUniversity != null) {
                    currentUniversity.setId(Integer.parseInt(snapshot.getKey()));
                    updateUniversityProfile();
                } else showErrorProfile();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { showErrorProfile(); }
        });
    }

    private void loadStudentData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String studentId = prefs.getString("STUDENT_ID²", null);

        if (studentId == null) {
            showErrorProfile();
            return;
        }

        studentsRef.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showDefaultProfile();
                    return;
                }

                currentStudent = snapshot.getValue(Student.class);
                if (currentStudent == null) {
                    showErrorProfile();
                    return;
                }

                currentStudent.setId(snapshot.getKey());
                currentUniversityId = currentStudent.getUniId();

                loadUniversityForStudent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showErrorProfile();
            }
        });
    }


    private void loadUniversityForStudent() {
        if (currentUniversityId == null) { showDefaultProfile(); return; }
        unisRef.child(currentUniversityId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUniversity = snapshot.getValue(Uni.class);
                if (currentUniversity != null) { currentUniversity.setId(Integer.parseInt(snapshot.getKey())); updateStudentProfile(); }
                else showDefaultProfile();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { showErrorProfile(); }
        });
    }

    // -------------------- Profile UI ----------------------------------------
    private void updateUniversityProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("University Admin");
            profileGroupTextView.setText(currentUniversity != null ? currentUniversity.getName() : "Administrator");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            // University admin sees everything *except* admin panels
            updateMenuVisibility(false, true); // admin group hidden, student group visible
        });
    }

    private void updateDebugAdminProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Debug Admin");
            profileGroupTextView.setText("Developer Mode");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            // Debug admin sees everything
            updateMenuVisibility(true, true); // both admin and student groups visible
        });
    }

    private void updateStudentProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText(currentStudent.getFullName().isEmpty() ? "Unknown" : currentStudent.getFullName());
            profileGroupTextView.setText(currentUniversity != null ? currentUniversity.getName() : "Unknown University");
            loadProfileImage(currentStudent.getProfilePictureUri());
            // Student (non-admin) sees student group only; admins see both groups
            boolean showAdmin = currentStudent.isAdmin() || loginType == LoginType.DEBUG_ADMIN;
            updateMenuVisibility(showAdmin, true);
        });
    }


    private void updateMenuVisibility(boolean showAdminItems, boolean showStudentItems) {
        Menu menu = navigationView.getMenu();
        menu.setGroupVisible(R.id.group_common,true);
        menu.setGroupVisible(R.id.group_admin,showAdminItems);
        menu.setGroupVisible(R.id.group_student,showStudentItems);
    }

    private void loadProfileImage(String uri) {
        if (uri!=null && !uri.isEmpty()) {
            Glide.with(this).load(Uri.parse(uri)).placeholder(R.drawable.nav_profile_pic).error(R.drawable.nav_profile_pic).circleCrop().into(profileImageView);
        } else profileImageView.setImageResource(R.drawable.nav_profile_pic);
    }

    // -------------------- Navigation ----------------------------------------
    private void setupInitialFragment() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastFragment = prefs.getString("last_fragment", "home");

        Fragment fragmentToLoad;
        int menuIdToCheck;

        switch (lastFragment) {
            case "settings":
                fragmentToLoad = new SettingsFragment();
                menuIdToCheck = R.id.nav_settings;
                break;
            case "post":
                fragmentToLoad = CreatePostFragment.newInstance(loginType, currentUniversityId);
                menuIdToCheck = R.id.nav_post;
                break;
            case "admin":
                fragmentToLoad = new AdminFragment();
                menuIdToCheck = R.id.nav_admin;
                break;
            case "profile":
                fragmentToLoad = new ProfileFragment();
                menuIdToCheck = R.id.nav_profile;
                break;
            case "clubs":
                fragmentToLoad = new ClubsFragment();
                menuIdToCheck = R.id.nav_clubs;
                break;
            case "schedule":
                fragmentToLoad = new ScheduleFragment();
                menuIdToCheck = R.id.nav_schedule;
                break;
            case "scores":
                fragmentToLoad = new ScoresFragment();
                menuIdToCheck = R.id.nav_scores;
                break;
            default:
                fragmentToLoad = HomeFragment.newInstance(loginType, currentUniversityId);
                menuIdToCheck = R.id.nav_home;
                break;
        }

        navigateToFragment(fragmentToLoad, menuIdToCheck);
    }

    private String getFragmentTagFromMenuId(int id) {
        if (id == R.id.nav_home) return "home";
        else if (id == R.id.nav_settings) return "settings";
        else if (id == R.id.nav_post) return "post";
        else if (id == R.id.nav_admin) return "admin";
        else if (id == R.id.nav_profile) return "profile";
        else if (id == R.id.nav_clubs) return "clubs";
        else if (id == R.id.nav_schedule) return "schedule";
        else if (id == R.id.nav_scores) return "scores";
        else if (id == R.id.nav_messages) return "messages";
        else return "home";
    }



    private void navigateToFragment(Fragment f, int menuId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();

        // Save the tag of the last fragment
        String tag = getFragmentTagFromMenuId(menuId);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("last_fragment", tag)
                .apply();

        navigationView.setCheckedItem(menuId);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = HomeFragment.newInstance(loginType, currentUniversityId);
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        } else if (id == R.id.nav_logout) {
            setStudentOfflineAndLogout(); // <- replaced
            return true;
        } else if (id == R.id.nav_post) {
            if (hasPostingAccess()) fragment = CreatePostFragment.newInstance(loginType, currentUniversityId);
        } else if (id == R.id.nav_admin) {
            if (hasAdminAccess()) fragment = new AdminFragment();
        } else if (id == R.id.nav_profile) {
            if (hasStudentAccess()) fragment = new ProfileFragment();
        } else if (id == R.id.nav_clubs) {
            if (hasStudentAccess()) fragment = new ClubsFragment();
        } else if (id == R.id.nav_schedule) {
            if (hasStudentAccess()) fragment = new ScheduleFragment();
        } else if (id == R.id.nav_scores) {
            if (hasStudentAccess()) fragment = new ScoresFragment();
        else if (id == R.id.nav_messages) {
            if (hasStudentAccess()) fragment = new MessagesFragment();
        }

        }

        if (fragment != null) navigateToFragment(fragment, id);
        else showAccessDenied();

        return true;
    }
    // -------------------- Access Helpers ------------------------------------
    private boolean hasAdminAccess()   { return loginType==LoginType.DEBUG_ADMIN || (currentStudent!=null && currentStudent.isAdmin()); }
    private boolean hasPostingAccess() { return loginType==LoginType.UNIVERSITY_ADMIN || hasAdminAccess(); }
    private boolean hasStudentAccess() { return loginType==LoginType.REGULAR_STUDENT || loginType==LoginType.STUDENT_ADMIN; }

    private void showAccessDenied(){ Toast.makeText(this,"Access denied",Toast.LENGTH_SHORT).show(); }

    // -------------------- Logout --------------------------------------------
    private void handleLogout() {
        Log.d("Logout", "Logging out user...");

        // Set student as offline in Firebase
        if ((loginType == LoginType.REGULAR_STUDENT || loginType == LoginType.STUDENT_ADMIN) && currentStudent != null && currentStudent.getId() != null) {
            studentsRef.child(currentStudent.getId()).child("isOnline").setValue(false);
        }

        // Clear shared preferences to ensure no user session remains
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("is_logged_in", false).apply(); // Set logged-in state to false
        prefs.edit().remove("student_id").apply(); // Optionally remove student ID
        prefs.edit().remove("last_fragment").apply(); // Remove the last fragment preference (this is key)

        // Show logout toast
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        // Start LoginActivity and clear task stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Log for debugging
        Log.d("Logout", "Finishing MainActivity");
        finish(); // Ensure MainActivity is completely finished
    }


    // -------------------- Back ----------------------------------------------
    private void setupBackButtonHandler(){
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
            @Override public void handleOnBackPressed(){
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START); else finish();
            }
        });
    }

    // -------------------- Error helpers -------------------------------------
    private void showDefaultProfile(){ runOnUiThread(()->{
        profileNameTextView.setText("No student logged in");
        profileGroupTextView.setText("N/A");
        profileImageView.setImageResource(R.drawable.nav_profile_pic);
        updateMenuVisibility(false,false);
    }); }

    private void showErrorProfile(){ runOnUiThread(()->{
        profileNameTextView.setText("Error loading profile");
        profileGroupTextView.setText("N/A");
        profileImageView.setImageResource(R.drawable.nav_profile_pic);
        updateMenuVisibility(false,false);
    }); }

    private void setStudentOfflineAndLogout() {
        if (currentStudent != null && currentStudent.getId() != null) {
            studentsRef.child(currentStudent.getId()).child("isOnline").setValue(false).addOnCompleteListener(task -> {
                clearFragmentBackStack();
                handleLogout();
            });
        } else {
            clearFragmentBackStack();
            handleLogout();
        }
    }

    private void clearFragmentBackStack() {
        // Clear all fragments in the back stack
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
