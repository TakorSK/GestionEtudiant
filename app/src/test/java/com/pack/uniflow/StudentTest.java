package com.pack.uniflow;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Local unit test for the Student class, focusing on constructor logic.
 *
 * @see Student
 */
public class StudentTest {

    // Define common inputs for the constructor to avoid repetition
    private int testId = 1;
    private String testEmail = "test@example.com";
    private String testFullName = "Test User";
    private int testAge = 20;
    private String testTelephone = "123456";
    private int testUniId = 1;
    private String testPassword = "password123";

    private Student student; // Instance to be tested

    @Before // Method to run before each test
    public void setUp() {
        // Explanation: Create a new Student instance using the updated 7-argument
        // constructor before each test method runs.
        student = new Student(
                testId, testEmail, testFullName, testAge,
                testTelephone, testUniId, testPassword
        );
    }

    @Test
    public void constructor_setsRegistrationDateCorrectly() {
        // Explanation: This test verifies that the Student constructor correctly
        // initializes the registrationDate field with the current date in the
        // expected "yyyy-MM-dd" format. It uses standard Java classes.
        // This test remains relevant for the updated constructor.

        // Arrange: Define expected date format pattern and today's date string
        Pattern expectedDateFormat = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        String expectedDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Act: (Student instance created in setUp)

        // Assert: Check if the registrationDate is not null, matches the format,
        // and matches today's date string.
        assertNotNull("Registration date should not be null", student.registrationDate);
        assertTrue("Registration date format should be yyyy-MM-dd",
                expectedDateFormat.matcher(student.registrationDate).matches());
        assertEquals("Registration date should be today's date",
                expectedDateString, student.registrationDate);
    }

    @Test
    public void constructor_setsDefaultValuesCorrectly() {
        // Explanation: This NEW test verifies that the constructor correctly
        // initializes fields that have default values set within the constructor body
        // (isAdmin, profilePictureUri) or fields not set by the constructor (Bio).

        // Arrange: (Student instance created in setUp)

        // Act: (Student instance created in setUp)

        // Assert: Check the default values
        assertFalse("isAdmin should default to false", student.isAdmin);
        assertEquals("profilePictureUri should default to empty string", "", student.profilePictureUri);
        assertNull("Bio should be null as it's not set by constructor", student.Bio);
    }

    @Test
    public void constructor_assignsArgumentsCorrectly() {
        // Explanation: This NEW test verifies that the basic arguments passed
        // to the constructor are correctly assigned to the corresponding fields.

        // Arrange: (Student instance created in setUp using test values)

        // Act: (Student instance created in setUp)

        // Assert: Check if the fields match the input arguments
        assertEquals("ID should match constructor argument", testId, student.id);
        assertEquals("Email should match constructor argument", testEmail, student.email);
        assertEquals("Full Name should match constructor argument", testFullName, student.fullName);
        assertEquals("Age should match constructor argument", testAge, student.age);
        assertEquals("Telephone should match constructor argument", testTelephone, student.telephone);
        assertEquals("Uni ID should match constructor argument", testUniId, student.uniId);
        assertEquals("Password should match constructor argument", testPassword, student.password);
    }
}