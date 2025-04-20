package com.pack.uniflow.Activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore; // Import MediaStore

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule; // For permissions

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni; // Needed for Student FK
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.StudentDao; // Import DAOs
import com.pack.uniflow.UniDao;
// import com.pack.uniflow.util.EspressoIdlingResource; // Import your IdlingResource

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Instrumented tests for ChangeProfileActivity (Database Version).
 */
@RunWith(AndroidJUnit4.class)
public class ChangeProfileActivityTest {

    private UniflowDB db;
    private StudentDao studentDao;
    private UniDao uniDao;
    private Context context;
    private Student testStudent;
    private long testUniId;

    // Rule to launch the activity under test
    @Rule
    public ActivityScenarioRule<ChangeProfileActivity> activityRule =
            new ActivityScenarioRule<>(ChangeProfileActivity.class);

    // Rule to automatically grant permissions for tests
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    // --- Test Setup ---
    @Before
    public void setUp() {
        Intents.init();
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        // DatabaseClient.replaceInstance(db); // Commented out
        studentDao = db.studentDao();
        uniDao = db.uniDao();

        db.clearAllTables();
        // Insert prerequisite Uni
        Uni uni = new Uni(); uni.name = "Profile Test Uni";
        testUniId = uniDao.insert(uni);

        // Create and insert the 'online' student
        testStudent = new Student(101, "online@test.com", "Online User", 22, "11122233", (int)testUniId, "password");
        testStudent.isOnline = true;
        testStudent.Bio = "Initial Bio From DB";
        testStudent.profilePictureUri = null; // Start with no picture
        studentDao.insert(testStudent);

        // Register Idling Resource (Replace with your actual implementation)
        // IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws IOException {
        Intents.release();
        db.close();
        // DatabaseClient.restoreInstance(); // Commented out
        // Unregister Idling Resource
        // IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        cleanupTestImages(); // Clean up saved images
    }

    // Helper to clean up images saved to internal storage during tests
    private void cleanupTestImages() {
        File storageDir = context.getFilesDir();
        if (storageDir != null && storageDir.exists()) {
            File[] files = storageDir.listFiles((dir, name) -> name.startsWith("profile_") && name.endsWith(".jpg"));
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    // --- Test Cases ---

    @Test
    public void testInitialLoad_DisplaysDataFromDatabase() {
        // Explanation: Verifies initial loading of Bio from the DB student.
        // Assumes IdlingResource handles waiting for the background load.

        // Assert: Check EditText displays the initial bio from DB
        onView(withId(R.id.edt_bio)).check(matches(withText("Initial Bio From DB")));
        // Assert: Check ImageView is displayed (placeholder initially)
        onView(withId(R.id.iv_profile_picture)).check(matches(isDisplayed()));
    }

    @Test
    public void testInitialLoad_WithProfilePicture_DisplaysImage() {
        // Explanation: Tests initial load when the DB student has a picture URI.
        // Arrange: Update student in DB with a URI (use a drawable resource for testing)
        Uri fakeImageUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.nav_profile_pic);
        testStudent.profilePictureUri = fakeImageUri.toString();
        studentDao.update(testStudent);

        // Act: Relaunch activity to trigger onCreate with new data
        activityRule.getScenario().recreate(); // Triggers reload

        // Assert: Check bio and image view (assumes IdlingResource waits)
        onView(withId(R.id.edt_bio)).check(matches(withText("Initial Bio From DB")));
        onView(withId(R.id.iv_profile_picture)).check(matches(isDisplayed()));
        // Verifying the *correct* image loaded by Glide is complex.
    }


    @Test
    public void testClickProfileImage_OpensImagePickerIntent() {
        // Explanation: Verifies clicking image view launches ACTION_PICK intent
        // (permission granted by rule).

        // Arrange: Stub the intent result
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Act: Click the profile image
        onView(withId(R.id.iv_profile_picture)).perform(click());

        // Assert: Verify the correct Intent was sent
        intended(allOf(
                hasAction(Intent.ACTION_PICK),
                hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // Check specific URI
        ));
    }

    @Test
    public void testActivityResult_DisplaysImagePreview() {
        // Explanation: Simulates image picker result, verifies preview update.

        // Arrange: Create fake result URI and Intent
        Uri fakeResultUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        Intent resultData = new Intent();
        resultData.setData(fakeResultUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Act: Click image view to trigger flow
        onView(withId(R.id.iv_profile_picture)).perform(click());

        // Assert: Check ImageView is displayed. More specific Glide checks are hard.
        onView(withId(R.id.iv_profile_picture)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveBioOnly_UpdatesDatabaseAndFinishes() {
        // Explanation: Enters new bio, saves, verifies DB Bio updated, pic URI null, finishes.

        // Arrange
        String newBio = "Updated Bio via Test";

        // Act
        onView(withId(R.id.edt_bio)).perform(replaceText(newBio), closeSoftKeyboard());
        onView(withId(R.id.btn_save)).perform(click());

        // Assert: (IdlingResource needed here!)
        // 1. Check Database
        // try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student updatedStudent = studentDao.getStudentById(testStudent.id);
        assertNotNull(updatedStudent);
        assertEquals("Bio should be updated in DB", newBio, updatedStudent.Bio);
        assertNull("Profile picture URI should remain null", updatedStudent.profilePictureUri);

        // 2. Check Activity finished and result OK
        assertEquals(Activity.RESULT_OK, activityRule.getScenario().getResult().getResultCode());
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }

    @Test
    public void testSavePictureOnly_UpdatesDatabaseAndFinishes() {
        // Explanation: Selects picture, saves, verifies DB pic URI updated (file URI), Bio unchanged, finishes.

        // Arrange: Simulate image selection
        Uri fakeResultUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        Intent resultData = new Intent();
        resultData.setData(fakeResultUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);
        onView(withId(R.id.iv_profile_picture)).perform(click()); // Trigger selection

        // Act: Click save
        onView(withId(R.id.btn_save)).perform(click());

        // Assert: (IdlingResource needed here!)
        // 1. Check Database
        // try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! (File I/O takes time)
        Student updatedStudent = studentDao.getStudentById(testStudent.id);
        assertNotNull(updatedStudent);
        assertEquals("Bio should NOT be updated in DB", "Initial Bio From DB", updatedStudent.Bio);
        assertNotNull("Profile picture URI should be updated", updatedStudent.profilePictureUri);
        assertTrue("Profile picture URI should be a file URI starting with file://", updatedStudent.profilePictureUri.startsWith("file://"));
        assertTrue("Profile picture URI should contain app's file dir name", updatedStudent.profilePictureUri.contains(context.getFilesDir().getName()));
        assertTrue("Profile picture URI should end with .jpg", updatedStudent.profilePictureUri.endsWith(".jpg"));

        // 2. Check Activity finished and result OK
        assertEquals(Activity.RESULT_OK, activityRule.getScenario().getResult().getResultCode());
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }

    @Test
    public void testSaveBothBioAndPicture_UpdatesDatabaseAndFinishes() {
        // Explanation: Changes bio and picture, saves, verifies both updated in DB, finishes.

        // Arrange:
        // 1. Simulate image selection
        Uri fakeResultUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        Intent resultData = new Intent();
        resultData.setData(fakeResultUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);
        onView(withId(R.id.iv_profile_picture)).perform(click());
        // 2. Define new bio
        String newBio = "New Bio and New Pic Test";

        // Act: Enter text and click save
        onView(withId(R.id.edt_bio)).perform(replaceText(newBio), closeSoftKeyboard());
        onView(withId(R.id.btn_save)).perform(click());

        // Assert: (IdlingResource needed here!)
        // 1. Check Database
        // try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky!
        Student updatedStudent = studentDao.getStudentById(testStudent.id);
        assertNotNull(updatedStudent);
        assertEquals("Bio should be updated in DB", newBio, updatedStudent.Bio);
        assertNotNull("Profile picture URI should be updated", updatedStudent.profilePictureUri);
        assertTrue("Profile picture URI should be a file URI", updatedStudent.profilePictureUri.startsWith("file://"));

        // 2. Check Activity finished and result OK
        assertEquals(Activity.RESULT_OK, activityRule.getScenario().getResult().getResultCode());
        assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }
}