package com.pack.uniflow.Activities;

import android.content.Context;
import android.content.Intent; // Import Intent for onBackPressed test
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack; // Import pressBack
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.pack.uniflow.DatabaseClient;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;
import com.pack.uniflow.Uni;
import com.pack.uniflow.UniflowDB;
import com.pack.uniflow.StudentDao;
import com.pack.uniflow.UniDao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays; // For creating list
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityRule =
            new ActivityScenarioRule<>(SignupActivity.class);

    private UniflowDB db;
    private Context context;
    private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private UniDao uniDao; // Make DAOs class members for access in tests
    private StudentDao studentDao;
    private Uni testUni; // Keep track of the test Uni

    // Define IDs used in tests
    private static final int VALID_UNI_ID = 1;
    private static final int NON_EXISTENT_UNI_ID = 999;
    private static final int VALID_STUDENT_ID = 12345678;
    private static final int UNAUTHORIZED_STUDENT_ID = 87654321;
    private static final int EXISTING_STUDENT_ID = 11112222;
    private static final String EXISTING_EMAIL = "exists@test.com";
    private static final String VALID_EMAIL = "new@test.com";


    @Before
    public void setUp() {
        Intents.init();
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        // DatabaseClient.replaceInstance(db); // Commented out

        // Initialize DAOs
        uniDao = db.uniDao();
        studentDao = db.studentDao();

        db.clearAllTables();

        // Pre-populate University with an authorized student ID
        testUni = new Uni();
        testUni.name = "Signup Test Uni";
        // *** Setup associated IDs for the new check ***
        testUni.setAssociatedStudentIdList(Arrays.asList(VALID_STUDENT_ID, EXISTING_STUDENT_ID));
        // Insert returns the generated ID, but we'll assume it's VALID_UNI_ID for simplicity
        // or retrieve it if needed. For consistency, let's assume insert doesn't change ID if set.
        testUni.id = VALID_UNI_ID; // Manually set ID for predictability in tests
        uniDao.insert(testUni); // Assumes insert respects pre-set ID or handles conflicts

        // Pre-populate conflicting student data for specific tests
        Student existingIdStudent = new Student(EXISTING_STUDENT_ID, "other@test.com", "Existing Id User", 25, "1111111111", VALID_UNI_ID, "passId");
        studentDao.insert(existingIdStudent);
        Student existingEmailStudent = new Student(UNAUTHORIZED_STUDENT_ID, EXISTING_EMAIL, "Existing Email User", 26, "2222222222", VALID_UNI_ID, "passEmail");
        studentDao.insert(existingEmailStudent);

    }

    @After
    public void tearDown() throws IOException {
        Intents.release();
        db.close();
        // DatabaseClient.restoreInstance(); // Commented out
    }

    // --- Validation Tests (Updated Error Messages) ---

    // ID Tests (Exactly 8 digits)
    @Test public void testId_TooShort_ShowsError() {
        onView(withId(R.id.signup_id)).perform(scrollTo(), replaceText("1234567"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_id)).check(matches(withText("ID must be 8 digits"))); // Updated msg
    }
    @Test public void testId_TooLong_ShowsError() {
        onView(withId(R.id.signup_id)).perform(scrollTo(), replaceText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_id)).check(matches(withText("ID must be 8 digits"))); // Updated msg
    }
    @Test public void testId_WithLetters_ShowsError() {
        onView(withId(R.id.signup_id)).perform(scrollTo(), replaceText("1234567a"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_id)).check(matches(withText("ID must be 8 digits"))); // Updated msg
    }

    // Name Tests (Letters/Spaces, min 2 chars)
    @Test public void testName_Empty_ShowsError() {
        onView(withId(R.id.signup_name)).perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_name)).check(matches(withText("Enter valid name (letters only, min 2 chars)"))); // Activity msg
    }
    @Test public void testName_TooShort_ShowsError() {
        onView(withId(R.id.signup_name)).perform(scrollTo(), replaceText("A"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_name)).check(matches(withText("Enter valid name (letters only, min 2 chars)"))); // Activity msg
    }
    @Test public void testName_WithNumber_ShowsError() {
        onView(withId(R.id.signup_name)).perform(scrollTo(), replaceText("Test1"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_name)).check(matches(withText("Enter valid name (letters only, min 2 chars)"))); // Activity msg
    }
    @Test public void testName_WithSymbol_ShowsError() {
        onView(withId(R.id.signup_name)).perform(scrollTo(), replaceText("Test-"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_name)).check(matches(withText("Enter valid name (letters only, min 2 chars)"))); // Activity msg
    }
    // Note: Leading space test removed as current regex [a-zA-Z ]+ allows it if > 1 char total

    // Firstname Tests (similar to Name)
    @Test public void testFirstname_Empty_ShowsError() {
        onView(withId(R.id.signup_firstname)).perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_firstname)).check(matches(withText("Enter valid first name"))); // Activity msg
    }
    @Test public void testFirstname_TooShort_ShowsError() {
        onView(withId(R.id.signup_firstname)).perform(scrollTo(), replaceText("B"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_firstname)).check(matches(withText("Enter valid first name"))); // Activity msg
    }
    @Test public void testFirstname_WithNumber_ShowsError() {
        onView(withId(R.id.signup_firstname)).perform(scrollTo(), replaceText("First1"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_firstname)).check(matches(withText("Enter valid first name"))); // Activity msg
    }

    // Age Tests (18-100)
    @Test public void testAge_NotNumber_ShowsError() {
        onView(withId(R.id.signup_age)).perform(scrollTo(), replaceText("abc"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_age)).check(matches(withText("Enter valid age"))); // Activity msg
    }
    @Test public void testAge_TooLow_ShowsError() {
        onView(withId(R.id.signup_age)).perform(scrollTo(), replaceText("17"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_age)).check(matches(withText("Age must be 18-100"))); // Activity msg
    }
    @Test public void testAge_TooHigh_ShowsError() {
        onView(withId(R.id.signup_age)).perform(scrollTo(), replaceText("101"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_age)).check(matches(withText("Age must be 18-100"))); // Activity msg
    }

    // Telephone Tests (8-15 digits)
    @Test public void testTelephone_Empty_ShowsError() {
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_telephone)).check(matches(withText("Enter valid phone (8-15 digits)"))); // Updated msg
    }
    @Test public void testTelephone_TooShort_ShowsError() {
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText("1234567"), closeSoftKeyboard()); // 7 digits
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_telephone)).check(matches(withText("Enter valid phone (8-15 digits)"))); // Updated msg
    }
    @Test public void testTelephone_TooLong_ShowsError() {
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText("1234567890123456"), closeSoftKeyboard()); // 16 digits
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_telephone)).check(matches(withText("Enter valid phone (8-15 digits)"))); // Updated msg
    }
    @Test public void testTelephone_WithLetters_ShowsError() {
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText("1234567a"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_telephone)).check(matches(withText("Enter valid phone (8-15 digits)"))); // Updated msg
    }
    @Test public void testTelephone_WithSymbols_ShowsError() {
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText("123-45678"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_telephone)).check(matches(withText("Enter valid phone (8-15 digits)"))); // Updated msg
    }

    // Password Tests (min 6 chars)
    @Test public void testPassword_TooShort_ShowsError() {
        onView(withId(R.id.signup_password)).perform(scrollTo(), replaceText("12345"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_password)).check(matches(withText("Password must be ≥6 characters"))); // Activity msg
    }

    // Email Tests (Valid format)
    @Test public void testEmail_Empty_ShowsError() {
        onView(withId(R.id.signup_email)).perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_email)).check(matches(withText("Enter valid email"))); // Activity msg
    }
    @Test public void testEmail_InvalidFormat_ShowsError() {
        onView(withId(R.id.signup_email)).perform(scrollTo(), replaceText("invalid-email"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_email)).check(matches(withText("Enter valid email"))); // Activity msg
    }
    // Note: Specific missing @ or domain tests might show the same generic message

    // University ID Tests (Digits only)
    @Test public void testUniversityId_Empty_ShowsError() {
        onView(withId(R.id.signup_university_id)).perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_university_id)).check(matches(withText("Enter valid university ID"))); // Activity msg
    }
    @Test public void testUniversityId_NotNumber_ShowsError() {
        onView(withId(R.id.signup_university_id)).perform(scrollTo(), replaceText("abc"), closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        onView(withId(R.id.error_signup_university_id)).check(matches(withText("Enter valid university ID"))); // Activity msg
    }

    // Test for Valid Input (No Initial Validation Errors Shown)
    @Test
    public void testValidInput_ShowsNoInitialValidationErrors() {
        fillValidData(); // Use the updated helper
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());
        // Check that initial validation errors are NOT displayed
        onView(withId(R.id.error_signup_id)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_firstname)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_age)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_telephone)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_email)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_signup_university_id)).check(matches(not(isDisplayed())));
        // Note: DB checks might still show errors later in the process
    }


    // --- Database Pre-Check Tests ---

    @Test
    public void testSignup_WithNonExistentUniversity_ShowsErrorAndEnablesButton() {
        // Arrange: Fill with valid data but an invalid Uni ID
        fillDataWithSpecificIds(String.valueOf(VALID_STUDENT_ID), String.valueOf(NON_EXISTENT_UNI_ID), VALID_EMAIL);

        // Act
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());

        // Assert: Check the specific error view and button state (wait might be needed)
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! Use IdlingResource
        onView(withId(R.id.error_signup_university_id))
                .check(matches(isDisplayed()))
                .check(matches(withText("University not found")));
        onView(withId(R.id.signup_button)).check(matches(isEnabled())); // Check button is re-enabled
        // Verify MainActivity NOT launched
        activityRule.getScenario().onActivity(activity -> assertFalse(activity.isFinishing()));
    }

    @Test
    public void testSignup_WithUnauthorizedStudentId_ShowsErrorAndEnablesButton() {
        // Arrange: Fill with valid data but use an ID not associated with the Uni
        fillDataWithSpecificIds(String.valueOf(UNAUTHORIZED_STUDENT_ID), String.valueOf(VALID_UNI_ID), VALID_EMAIL);

        // Act
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());

        // Assert: Check the specific error view and button state (wait might be needed)
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! Use IdlingResource
        onView(withId(R.id.error_signup_id))
                .check(matches(isDisplayed()))
                .check(matches(withText("ID not authorized for this university")));
        onView(withId(R.id.signup_button)).check(matches(isEnabled()));
        activityRule.getScenario().onActivity(activity -> assertFalse(activity.isFinishing()));
    }


    @Test
    public void testSignup_WithExistingStudentId_ShowsErrorAndEnablesButton() {
        // Arrange: Fill form with valid data but use the existing student ID
        fillDataWithSpecificIds(String.valueOf(EXISTING_STUDENT_ID), String.valueOf(VALID_UNI_ID), VALID_EMAIL);

        // Act
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());

        // Assert: Check the specific error view and button state (wait might be needed)
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! Use IdlingResource
        onView(withId(R.id.error_signup_id))
                .check(matches(isDisplayed()))
                .check(matches(withText("ID already registered")));
        onView(withId(R.id.signup_button)).check(matches(isEnabled()));
        activityRule.getScenario().onActivity(activity -> assertFalse(activity.isFinishing()));
    }

    @Test
    public void testSignup_WithExistingEmail_ShowsErrorAndEnablesButton() {
        // Arrange: Fill form with valid data but use the existing email
        fillDataWithSpecificIds(String.valueOf(VALID_STUDENT_ID), String.valueOf(VALID_UNI_ID), EXISTING_EMAIL);

        // Act
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());

        // Assert: Check the specific error view and button state (wait might be needed)
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! Use IdlingResource
        onView(withId(R.id.error_signup_email))
                .check(matches(isDisplayed()))
                .check(matches(withText("Email already registered")));
        onView(withId(R.id.signup_button)).check(matches(isEnabled()));
        activityRule.getScenario().onActivity(activity -> assertFalse(activity.isFinishing()));
    }


    // --- Success Path Test ---
    @Test
    public void testSuccessfulSignup_NavigatesToMainAndAddsStudentWithCorrectState() {
        // Arrange
        fillValidData(); // Uses updated valid data that should pass all checks

        // Act
        onView(withId(R.id.signup_button)).perform(scrollTo(), click());

        // Assert
        // 1. Verify navigation
        intended(hasComponent(MainActivity.class.getName()));
        // 2. Verify student added to DB and state is correct
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // Flaky! Use IdlingResource
        Student signedUpStudent = studentDao.findByEmail(VALID_EMAIL); // Use email from fillValidData
        assertNotNull(signedUpStudent);
        assertEquals(VALID_STUDENT_ID, signedUpStudent.id); // Use ID from fillValidData
        assertEquals("Valid Name WithSpace", signedUpStudent.fullName); // Updated expected name
        assertEquals(VALID_UNI_ID, signedUpStudent.uniId); // Check against the Uni ID used
        assertTrue(signedUpStudent.isOnline);
        assertNotNull(signedUpStudent.lastLogin);
        assertTrue(DATE_FORMAT_PATTERN.matcher(signedUpStudent.lastLogin).matches());
        assertNotNull(signedUpStudent.registrationDate);
        assertTrue(DATE_FORMAT_PATTERN.matcher(signedUpStudent.registrationDate).matches());
    }

    // --- Back Button Test ---
    @Test
    public void testBackButton_NavigatesToLogin() {
        // Act
        pressBack();

        // Assert
        intended(hasComponent(LoginActivity.class.getName()));
        // Check if current activity finished (optional, depends on exact back behavior)
        // assertTrue(activityRule.getScenario().getState().isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED));
    }


    // --- Helper Methods (Updated telephone, uses constants) ---
    private void fillDataWithSpecificIds(String studentId, String uniId, String email) {
        onView(withId(R.id.signup_id)).perform(scrollTo(), replaceText(studentId));
        onView(withId(R.id.signup_name)).perform(scrollTo(), replaceText("Valid Name"));
        onView(withId(R.id.signup_firstname)).perform(scrollTo(), replaceText("WithSpace"));
        onView(withId(R.id.signup_age)).perform(scrollTo(), replaceText("25"));
        onView(withId(R.id.signup_telephone)).perform(scrollTo(), replaceText("0123456789")); // 10 digits (valid 8-15)
        onView(withId(R.id.signup_password)).perform(scrollTo(), replaceText("password123"));
        onView(withId(R.id.signup_email)).perform(scrollTo(), replaceText(email));
        onView(withId(R.id.signup_university_id)).perform(scrollTo(), replaceText(uniId), closeSoftKeyboard());
    }

    private void fillValidData() {
        // Helper to fill with data expected to pass all checks
        fillDataWithSpecificIds(String.valueOf(VALID_STUDENT_ID), String.valueOf(VALID_UNI_ID), VALID_EMAIL);
    }
}