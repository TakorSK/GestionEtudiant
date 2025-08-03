package com.pack.uniflow.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pack.uniflow.FirebaseConfig;
import com.pack.uniflow.Fragments.AdminFragment;
import com.pack.uniflow.Fragments.ClubsFragment;
import com.pack.uniflow.Fragments.CreatePostFragment;
import com.pack.uniflow.Fragments.HomeFragment;
import com.pack.uniflow.Fragments.ProfileFragment;
import com.pack.uniflow.Fragments.ScheduleFragment;
import com.pack.uniflow.Fragments.SettingsFragment;
import com.pack.uniflow.Fragments.ScoresFragment;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        studentsRef.orderByChild("isOnline").equalTo(true).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) { showDefaultProfile(); return; }
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            currentStudent = snap.getValue(Student.class);
                            if (currentStudent == null) continue;
                            currentStudent.setId(snap.getKey());
                            currentUniversityId = currentStudent.getUniId();
                            loadUniversityForStudent();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { showErrorProfile(); }
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
            updateMenuVisibility(false,true); // post visible
        });
    }

    private void updateDebugAdminProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText("Debug Admin");
            profileGroupTextView.setText("Developer Mode");
            profileImageView.setImageResource(R.drawable.nav_profile_pic);
            updateMenuVisibility(true,false);
        });
    }

    private void updateStudentProfile() {
        runOnUiThread(() -> {
            profileNameTextView.setText(currentStudent.getFullName().isEmpty()?"Unknown":currentStudent.getFullName());
            profileGroupTextView.setText(currentUniversity!=null?currentUniversity.getName():"Unknown University");
            loadProfileImage(currentStudent.getProfilePictureUri());
            updateMenuVisibility(currentStudent.isAdmin()||loginType==LoginType.DEBUG_ADMIN,true);
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
        navigateToFragment(HomeFragment.newInstance(loginType,currentUniversityId), R.id.nav_home);
    }

    private void navigateToFragment(Fragment f,int menuId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,f).commit();
        navigationView.setCheckedItem(menuId);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = HomeFragment.newInstance(loginType,currentUniversityId);
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        } else if (id == R.id.nav_logout) {
            handleLogout();
            return true;
        } else if (id == R.id.nav_post) {
            if (hasPostingAccess()) fragment = CreatePostFragment.newInstance(loginType,currentUniversityId);
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
        }

        if (fragment != null) navigateToFragment(fragment,id); else showAccessDenied();
        return true;
    }

    // -------------------- Access Helpers ------------------------------------
    private boolean hasAdminAccess()   { return loginType==LoginType.DEBUG_ADMIN || (currentStudent!=null && currentStudent.isAdmin()); }
    private boolean hasPostingAccess() { return loginType==LoginType.UNIVERSITY_ADMIN || hasAdminAccess(); }
    private boolean hasStudentAccess() { return loginType==LoginType.REGULAR_STUDENT || loginType==LoginType.STUDENT_ADMIN; }

    private void showAccessDenied(){ Toast.makeText(this,"Access denied",Toast.LENGTH_SHORT).show(); }

    // -------------------- Logout --------------------------------------------
    private void handleLogout(){
        if ((loginType==LoginType.REGULAR_STUDENT || loginType==LoginType.STUDENT_ADMIN) && currentStudent!=null && currentStudent.getId()!=null){
            studentsRef.child(currentStudent.getId()).child("isOnline").setValue(false);
        }
        Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
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
        profileNameTextView.setText("Error");
        profileGroupTextView.setText("Loading failed");
        profileImageView.setImageResource(R.drawable.nav_profile_pic);
        updateMenuVisibility(false,false);
        Toast.makeText(this,"Error loading profile",Toast.LENGTH_SHORT).show();
    }); }

    @Override protected void onDestroy(){ super.onDestroy(); executorService.shutdown(); }
}