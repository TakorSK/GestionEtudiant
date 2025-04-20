package com.pack.uniflow.Models;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Local unit tests for the Post model class, focusing on constructor logic.
 *
 * @see Post
 */
public class PostTest {

    @Test
    public void constructor_assignsFieldsAndFormatsDateCorrectly() {
        // Explanation: This test verifies that the parameterized constructor correctly
        // assigns the passed arguments (title, description, imageUri, authorId)
        // AND that it correctly calculates and formats the 'createdAt' field
        // into the "yyyy-MM-dd HH:mm:ss" format using the default locale.

        // Arrange: Define input values and expected date format
        String expectedTitle = "Test Post Title";
        String expectedDesc = "A description for the test post.";
        String expectedUri = "content://image/1";
        int expectedAuthorId = 101;
        // Define the expected date/time format pattern
        Pattern expectedDateTimeFormat = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
        // Get today's date part for comparison (optional sanity check)
        String expectedDatePart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        // Act: Create a new Post instance using the parameterized constructor
        Post post = new Post(expectedTitle, expectedDesc, expectedUri, expectedAuthorId);

        // Assert: Verify standard field assignments
        assertEquals("Title should match constructor argument", expectedTitle, post.getTitle());
        assertEquals("Description should match constructor argument", expectedDesc, post.getDescription());
        assertEquals("Image URI should match constructor argument", expectedUri, post.getImageUri());
        assertEquals("Author ID should match constructor argument", expectedAuthorId, post.getAuthorId());

        // Assert: Verify createdAt field
        assertNotNull("createdAt should not be null", post.getCreatedAt());
        assertTrue("createdAt format should be yyyy-MM-dd HH:mm:ss",
                expectedDateTimeFormat.matcher(post.getCreatedAt()).matches());
        // Optional: Check if the date part matches today's date
        assertTrue("createdAt date part should match today's date",
                post.getCreatedAt().startsWith(expectedDatePart));
    }

    @Test
    public void constructor_handlesNullDescription() {
        // Explanation: Verifies that passing null for the optional description
        // results in the description field being null.

        // Arrange
        String expectedTitle = "Null Desc Post";
        String expectedUri = "content://image/2";
        int expectedAuthorId = 102;

        // Act
        Post post = new Post(expectedTitle, null, expectedUri, expectedAuthorId); // Pass null description

        // Assert
        assertEquals(expectedTitle, post.getTitle());
        assertNull("Description should be null when null is passed to constructor", post.getDescription());
        assertEquals(expectedUri, post.getImageUri());
        assertEquals(expectedAuthorId, post.getAuthorId());
        assertNotNull(post.getCreatedAt()); // createdAt should still be set
    }
}