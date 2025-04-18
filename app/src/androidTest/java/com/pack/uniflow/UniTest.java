package com.pack.uniflow;

// AndroidX Test imports for Instrumented Tests
import androidx.test.ext.junit.runners.AndroidJUnit4;

// Android SDK classes are now available
import android.text.TextUtils;

// Standard JUnit imports remain the same
import org.junit.Test;
import org.junit.runner.RunWith; // Import RunWith
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Instrumented test for helper methods in the Uni class.
 * This runs on a device or emulator, providing access to Android SDK classes like TextUtils.
 *
 * @see Uni
 */
@RunWith(AndroidJUnit4.class) // Specify the AndroidJUnit4 runner
public class UniTest {

    // No @Before or @After needed here as we are only testing pure logic
    // within the Uni object and don't need Context or DB setup for these specific tests.

    // --- Tests for getAssociatedStudentIdList ---

    @Test
    public void getAssociatedStudentIdList_withNullString_returnsEmptyList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = null;

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withEmptyString_returnsEmptyList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = "";

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withWhitespaceString_returnsEmptyList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = "   "; // Whitespace only

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withSingleValidId_returnsCorrectList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = "123";
        List<Integer> expected = Collections.singletonList(123);

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withMultipleValidIds_returnsCorrectList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5,10";
        List<Integer> expected = Arrays.asList(1, 5, 10);

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withSpacesAndValidIds_returnsCorrectList() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = " 1 , 5 , 10 "; // Includes spaces
        List<Integer> expected = Arrays.asList(1, 5, 10);

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withInvalidEntries_ignoresInvalid() {
        // Arrange
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,abc,5, ,def,10,"; // Includes invalid and empty entries
        List<Integer> expected = Arrays.asList(1, 5, 10);

        // Act
        List<Integer> result = uni.getAssociatedStudentIdList();

        // Assert
        assertEquals(expected, result);
    }

    // --- Tests for setAssociatedStudentIdList ---

    @Test
    public void setAssociatedStudentIdList_withNullList_setsEmptyString() {
        // Explanation: TextUtils.join returns an empty string "" when passed a null list.
        // Arrange
        Uni uni = new Uni();
        List<Integer> ids = null; // Passing null list

        // Act
        uni.setAssociatedStudentIdList(ids);

        // Assert
        assertEquals("", uni.associatedStudentIds); // TextUtils.join(delimiter, null) returns ""
    }


    @Test
    public void setAssociatedStudentIdList_withEmptyList_setsEmptyString() {
        // Arrange
        Uni uni = new Uni();
        List<Integer> ids = new ArrayList<>();

        // Act
        uni.setAssociatedStudentIdList(ids);

        // Assert
        assertEquals("", uni.associatedStudentIds);
    }

    @Test
    public void setAssociatedStudentIdList_withSingleId_setsCorrectString() {
        // Arrange
        Uni uni = new Uni();
        List<Integer> ids = Collections.singletonList(123);

        // Act
        uni.setAssociatedStudentIdList(ids);

        // Assert
        assertEquals("123", uni.associatedStudentIds);
    }

    @Test
    public void setAssociatedStudentIdList_withMultipleIds_setsCorrectString() {
        // Arrange
        Uni uni = new Uni();
        List<Integer> ids = Arrays.asList(1, 5, 10);

        // Act
        uni.setAssociatedStudentIdList(ids);

        // Assert
        assertEquals("1,5,10", uni.associatedStudentIds);
    }
}