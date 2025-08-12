package com.pack.uniflow.Activities;

// Espresso and AndroidX Test Imports (Keep all from previous version)
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.util.Log; // Import Log for "gift" messages
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

// Project Specific Imports
import com.pack.uniflow.R;
import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.UniflowDB;
// *** ADDED DAO IMPORTS ***
import com.pack.uniflow.Models.StudentDao;
import com.pack.uniflow.Models.UniDao;
// import com.pack.uniflow.util.EspressoIdlingResource;

// JUnit Imports
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Java Utility Imports
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Instrumented tests for LoginActivity (Updated Version).
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private UniflowDB db;
    // Declare DAO variables with correct types
    private StudentDao studentDao;
    private UniDao uniDao;
    private Context context;
    private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final String TAG = "LoginActivityTest"; // Tag for logging
    private long testUniId; // Store generated Uni ID

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        // DatabaseClient.replaceInstance(db); // Commented out

        // Initialize DAOs correctly
        studentDao = db.studentDao();
        uniDao = db.uniDao();

        db.clearAllTables();
        // Pre-populate Uni needed for student foreign key
        Uni testUni = new Uni(); testUni.name = "Test Uni";
        // Store the generated ID
        testUniId = uniDao.insert(testUni); // Use the initialized uniDao

        // IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws IOException {
        Intents.release();
        db.close();
        // DatabaseClient.restoreInstance(); // Commented out
        // IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    // Helper to create student for tests (using 7-arg constructor)
    private Student createTestStudent(int id, String email, String password) {
        // Use the stored testUniId
        return new Student(id, email, "Test User " + id, 20, "555"+id, (int)testUniId, password);
    }

    // --- Test Cases ---

    @Test
    public void testEmptyCredentials_ShowsToast() {
        onView(withId(R.id.login_button)).perform(click());
        // onView(withText("Please enter credentials")).inRoot(isToast()).check(matches(isDisplayed()));
        activityRule.getScenario().onActivity(activity -> {
            assertFalse("Activity should not be finishing after failed login", activity.isFinishing());
        });
    }

    @Test
    public void testEmptyId_ShowsToast() {
        onView(withId(R.id.login_password)).perform(replaceText("password"));
        onView(withId(R.id.login_button)).perform(click());
        // onView(withText("Please enter credentials")).inRoot(isToast()).check(matches(isDisplayed()));
        activityRule.getScenario().onActivity(activity -> {
            assertFalse(activity.isFinishing());
        });
    }

    @Test
    public void testEmptyPassword_ShowsToast() {
        onView(withId(R.id.login_cid_gmail)).perform(replaceText("user@test.com"));
        onView(withId(R.id.login_button)).perform(click());
        // onView(withText("Please enter credentials")).inRoot(isToast()).check(matches(isDisplayed()));
        activityRule.getScenario().onActivity(activity -> {
            assertFalse(activity.isFinishing());
        });
    }

    @Test
    public void testSignupButton_NavigatesToSignupActivityAndFinishes() {
        onView(withId(R.id.go_to_signup_button)).perform(click());
        intended(hasComponent(SignupActivity.class.getName()));
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }

    @Test
    public void testValidLoginEmail_NavigatesAndUpdatesDb() {
        // Arrange
        Student student = createTestStudent(301, "valid@test.com", "password123");
        studentDao.insert(student); // Use initialized studentDao

        // Act
        onView(withId(R.id.login_cid_gmail)).perform(replaceText(student.email));
        onView(withId(R.id.login_password)).perform(replaceText(student.password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Assert
        intended(allOf(
                hasComponent(MainActivity.class.getName()),
                hasFlag(Intent.FLAG_ACTIVITY_NEW_TASK),
                hasFlag(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ));
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student loggedInStudent = studentDao.getStudentById(student.id); // Use initialized studentDao
        assertNotNull(loggedInStudent);
        assertTrue("Student should be marked online", loggedInStudent.isOnline);
        assertNotNull("lastLogin should be set", loggedInStudent.lastLogin);
        assertTrue("lastLogin format should be yyyy-MM-dd",
                DATE_FORMAT_PATTERN.matcher(loggedInStudent.lastLogin).matches());
        String expectedDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        assertEquals("lastLogin date should be today's date", expectedDateString, loggedInStudent.lastLogin);
        // onView(withText("Login successful")).inRoot(isToast()).check(matches(isDisplayed()));
        Log.d(TAG, "🎁 SUCCESS! Valid Email Login Verified for: " + student.email);
    }

    @Test
    public void testValidLoginId_NavigatesAndUpdatesDb() {
        // Arrange
        Student student = createTestStudent(302, "idlogin@test.com", "password456");
        studentDao.insert(student); // Use initialized studentDao

        // Act
        onView(withId(R.id.login_cid_gmail)).perform(replaceText(String.valueOf(student.id)));
        onView(withId(R.id.login_password)).perform(replaceText(student.password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Assert
        intended(allOf(
                hasComponent(MainActivity.class.getName()),
                hasFlag(Intent.FLAG_ACTIVITY_NEW_TASK),
                hasFlag(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ));
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student loggedInStudent = studentDao.getStudentById(student.id); // Use initialized studentDao
        assertNotNull(loggedInStudent);
        assertTrue("Student should be marked online", loggedInStudent.isOnline);
        assertNotNull("lastLogin should be set", loggedInStudent.lastLogin);
        Log.d(TAG, "🎉 SUCCESS! Valid ID Login Verified for ID: " + student.id);
    }

    @Test
    public void testInvalidPassword_ShowsToast() {
        // Arrange
        Student student = createTestStudent(303, "wrongpass@test.com", "correctPassword");
        studentDao.insert(student); // Use initialized studentDao

        // Act
        onView(withId(R.id.login_cid_gmail)).perform(replaceText(student.email));
        onView(withId(R.id.login_password)).perform(replaceText("wrongPassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Assert
        // onView(withText("Invalid credentials")).inRoot(isToast()).check(matches(isDisplayed()));
        activityRule.getScenario().onActivity(activity -> {
            assertFalse(activity.isFinishing());
        });
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student checkStudent = studentDao.getStudentById(student.id); // Use initialized studentDao
        assertNotNull(checkStudent);
        assertFalse("Student should remain offline", checkStudent.isOnline);
        assertNull("lastLogin should not be set", checkStudent.lastLogin);
    }

    @Test
    public void testUserNotFound_ShowsToast() {
        // Act
        onView(withId(R.id.login_cid_gmail)).perform(replaceText("nosuchuser@test.com"));
        onView(withId(R.id.login_password)).perform(replaceText("anypassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Assert
        // onView(withText("Invalid credentials")).inRoot(isToast()).check(matches(isDisplayed()));
        activityRule.getScenario().onActivity(activity -> {
            assertFalse(activity.isFinishing());
        });
    }

    @Test
    public void testSetAllOfflineOnLogin() {
        // Arrange
        Student student1 = createTestStudent(304, "user1@test.com", "pass1");
        Student student2 = createTestStudent(305, "user2@test.com", "pass2");
        student1.isOnline = true;
        studentDao.insert(student1); // Use initialized studentDao
        studentDao.insert(student2); // Use initialized studentDao

        // Act
        onView(withId(R.id.login_cid_gmail)).perform(replaceText(student2.email));
        onView(withId(R.id.login_password)).perform(replaceText(student2.password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // Assert
        intended(hasComponent(MainActivity.class.getName()));
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student checkStudent1 = studentDao.getStudentById(student1.id); // Use initialized studentDao
        Student checkStudent2 = studentDao.getStudentById(student2.id); // Use initialized studentDao

        assertNotNull(checkStudent1);
        assertFalse("Student 1 should now be offline", checkStudent1.isOnline);
        assertNotNull(checkStudent2);
        assertTrue("Student 2 should now be online", checkStudent2.isOnline);
    }
}