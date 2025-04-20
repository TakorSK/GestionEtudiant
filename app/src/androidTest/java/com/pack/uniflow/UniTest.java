package com.pack.uniflow;

// AndroidX Test imports for Instrumented Tests
import androidx.test.ext.junit.runners.AndroidJUnit4;

// Android SDK classes are now available
import android.text.TextUtils; // Keep this import

// Standard JUnit imports remain the same
import org.junit.Test;
import org.junit.runner.RunWith; // Import RunWith
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Instrumented test for helper methods in the Uni class (Updated).
 * This runs on a device or emulator, providing access to Android SDK classes like TextUtils.
 *
 * @see Uni
 */
@RunWith(AndroidJUnit4.class) // Specify the AndroidJUnit4 runner
public class UniTest {

    // No @Before or @After needed here as we are only testing pure logic
    // within the Uni object itself for these specific helper methods.

    // --- Tests for getAssociatedStudentIdList (Existing - Still Valid) ---

    @Test
    public void getAssociatedStudentIdList_withNullString_returnsEmptyList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = null;
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withEmptyString_returnsEmptyList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = "";
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withWhitespaceString_returnsEmptyList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = "   ";
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getAssociatedStudentIdList_withSingleValidId_returnsCorrectList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = "123";
        List<Integer> expected = Collections.singletonList(123);
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withMultipleValidIds_returnsCorrectList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5,10";
        List<Integer> expected = Arrays.asList(1, 5, 10);
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withSpacesAndValidIds_returnsCorrectList() {
        Uni uni = new Uni();
        uni.associatedStudentIds = " 1 , 5 , 10 ";
        List<Integer> expected = Arrays.asList(1, 5, 10);
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertEquals(expected, result);
    }

    @Test
    public void getAssociatedStudentIdList_withInvalidEntries_ignoresInvalid() {
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,abc,5, ,def,10,";
        List<Integer> expected = Arrays.asList(1, 5, 10);
        List<Integer> result = uni.getAssociatedStudentIdList();
        assertEquals(expected, result);
    }

    // --- Tests for setAssociatedStudentIdList (Existing - Still Valid) ---

    @Test
    public void setAssociatedStudentIdList_withNullList_setsEmptyString() {
        Uni uni = new Uni();
        List<Integer> ids = null;
        uni.setAssociatedStudentIdList(ids);
        assertEquals("", uni.associatedStudentIds);
    }


    @Test
    public void setAssociatedStudentIdList_withEmptyList_setsEmptyString() {
        Uni uni = new Uni();
        List<Integer> ids = new ArrayList<>();
        uni.setAssociatedStudentIdList(ids);
        assertEquals("", uni.associatedStudentIds);
    }

    @Test
    public void setAssociatedStudentIdList_withSingleId_setsCorrectString() {
        Uni uni = new Uni();
        List<Integer> ids = Collections.singletonList(123);
        uni.setAssociatedStudentIdList(ids);
        assertEquals("123", uni.associatedStudentIds);
    }

    @Test
    public void setAssociatedStudentIdList_withMultipleIds_setsCorrectString() {
        Uni uni = new Uni();
        List<Integer> ids = Arrays.asList(1, 5, 10);
        uni.setAssociatedStudentIdList(ids);
        assertEquals("1,5,10", uni.associatedStudentIds);
    }

    // --- NEW TESTS for containsStudentId ---

    @Test
    public void containsStudentId_whenListIsNull_returnsFalse() {
        // Explanation: Checks behavior when the underlying string is null.
        Uni uni = new Uni();
        uni.associatedStudentIds = null;
        assertFalse(uni.containsStudentId(123));
    }

    @Test
    public void containsStudentId_whenListIsEmpty_returnsFalse() {
        // Explanation: Checks behavior when the underlying string is empty.
        Uni uni = new Uni();
        uni.associatedStudentIds = "";
        assertFalse(uni.containsStudentId(123));
    }

    @Test
    public void containsStudentId_whenIdExists_returnsTrue() {
        // Explanation: Checks if it correctly finds an existing ID.
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5,10";
        assertTrue(uni.containsStudentId(5));
    }

    @Test
    public void containsStudentId_whenIdDoesNotExist_returnsFalse() {
        // Explanation: Checks if it correctly reports a non-existent ID.
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5,10";
        assertFalse(uni.containsStudentId(99));
    }

    @Test
    public void containsStudentId_withSpacesInString_returnsTrue() {
        // Explanation: Ensures trimming works correctly during the check.
        Uni uni = new Uni();
        uni.associatedStudentIds = " 1 , 5 , 10 ";
        assertTrue(uni.containsStudentId(5));
    }

    // --- NEW TESTS for addStudentId ---

    @Test
    public void addStudentId_toNullList_addsIdCorrectly() {
        // Explanation: Adds an ID when the initial string is null.
        Uni uni = new Uni();
        uni.associatedStudentIds = null;
        uni.addStudentId(123);
        assertEquals("123", uni.associatedStudentIds);
        assertTrue(uni.containsStudentId(123)); // Verify with contains method
    }

    @Test
    public void addStudentId_toEmptyList_addsIdCorrectly() {
        // Explanation: Adds an ID when the initial string is empty.
        Uni uni = new Uni();
        uni.associatedStudentIds = "";
        uni.addStudentId(456);
        assertEquals("456", uni.associatedStudentIds);
        assertTrue(uni.containsStudentId(456));
    }

    @Test
    public void addStudentId_toExistingList_addsNewIdCorrectly() {
        // Explanation: Adds a new, unique ID to a list that already has IDs.
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5";
        uni.addStudentId(10);
        // Order might vary depending on list implementation, check contents
        List<Integer> expectedIds = Arrays.asList(1, 5, 10);
        List<Integer> actualIds = uni.getAssociatedStudentIdList();
        assertTrue(actualIds.containsAll(expectedIds) && expectedIds.containsAll(actualIds));
        // Also check the string representation if order is predictable (often is)
        assertEquals("1,5,10", uni.associatedStudentIds);
    }

    @Test
    public void addStudentId_whenIdAlreadyExists_doesNotAddDuplicate() {
        // Explanation: Ensures adding an ID that's already present doesn't change the list/string.
        Uni uni = new Uni();
        uni.associatedStudentIds = "1,5,10";
        String originalString = uni.associatedStudentIds;
        uni.addStudentId(5); // Try to add existing ID 5
        assertEquals("String should not change when adding duplicate ID", originalString, uni.associatedStudentIds);
        assertEquals("List size should remain 3", 3, uni.getAssociatedStudentIdList().size());
    }
}