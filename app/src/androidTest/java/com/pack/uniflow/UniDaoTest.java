package com.pack.uniflow;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.Models.UniDao;

/**
 * Instrumented test for UniDao interface (Updated).
 */
@RunWith(AndroidJUnit4.class)
public class UniDaoTest {

    private UniflowDB db;
    private UniDao uniDao;
    private Context context;

    // Helper method to create a Uni object for testing
    // *** ASSUMES Uni.java now has a uniPassword field ***
    private Uni createTestUni(String name, String location, int year, String website, String password) {
        Uni uni = new Uni();
        // ID will be auto-generated on insert
        uni.name = name;
        uni.location = location;
        uni.establishedYear = year;
        uni.website = website;
        uni.uniPassword = password; // Assign the password
        // Assign other fields like associatedStudentIds if needed for other tests
        return uni;
    }

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        uniDao = db.uniDao();
        db.clearAllTables(); // Clear before each test
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // --- Existing Tests (Still Valid) ---

    @Test
    public void insertAndGetUniById() throws Exception {
        // Arrange
        Uni uni = createTestUni("Test University", "Test Location", 2000, "test.com", "pass123");

        // Act
        long generatedId = uniDao.insert(uni);
        // Test both equivalent methods
        Uni retrievedById = uniDao.getById((int) generatedId);
        Uni retrievedByUniId = uniDao.getUniversityById((int) generatedId);

        // Assert
        assertNotNull(retrievedById);
        assertEquals(uni.name, retrievedById.name);
        assertEquals((int) generatedId, retrievedById.id); // Verify ID matches

        assertNotNull(retrievedByUniId);
        assertEquals(uni.name, retrievedByUniId.name);
        assertEquals((int) generatedId, retrievedByUniId.id);
    }

    @Test
    public void insertAndFindByName() throws Exception {
        // Arrange
        Uni uni = createTestUni("FindMe University", "Somewhere", 1995, "find.me", "findPass");
        uniDao.insert(uni);

        // Act
        Uni foundUni = uniDao.findByName("FindMe University");

        // Assert
        assertNotNull(foundUni);
        assertEquals(uni.name, foundUni.name);
    }

    @Test
    public void getAllUnisWhenEmpty() throws Exception {
        // Act: Test both equivalent methods
        List<Uni> allUnis1 = uniDao.getAllUnis();
        List<Uni> allUnis2 = uniDao.getAll();

        // Assert
        assertNotNull(allUnis1);
        assertTrue(allUnis1.isEmpty());
        assertNotNull(allUnis2);
        assertTrue(allUnis2.isEmpty());
    }

    @Test
    public void insertMultipleAndGetAllUnis() throws Exception {
        // Arrange
        Uni uni1 = createTestUni("Uni One", "Loc 1", 2001, "uni1.org", "p1");
        Uni uni2 = createTestUni("Uni Two", "Loc 2", 2002, "uni2.org", "p2");
        uniDao.insert(uni1);
        uniDao.insert(uni2);

        // Act: Test both equivalent methods
        List<Uni> allUnis1 = uniDao.getAllUnis();
        List<Uni> allUnis2 = uniDao.getAll();

        // Assert
        assertNotNull(allUnis1);
        assertEquals(2, allUnis1.size());
        assertNotNull(allUnis2);
        assertEquals(2, allUnis2.size());
    }

    // --- NEW TESTS ---

    @Test
    public void getIdByName_returnsCorrectId() throws Exception {
        // Explanation: Verifies that the correct ID is returned when searching by name.
        // Arrange
        String targetName = "Target Uni";
        Uni uni1 = createTestUni("Other Uni", "Loc X", 2010, "other.com", "px");
        Uni uni2 = createTestUni(targetName, "Loc T", 2011, "target.com", "pt");
        uniDao.insert(uni1);
        long targetGeneratedId = uniDao.insert(uni2); // Get the ID of the target

        // Act
        int retrievedId = uniDao.getIdByName(targetName);

        // Assert
        assertEquals("Retrieved ID should match the generated ID", (int)targetGeneratedId, retrievedId);
    }

    @Test
    public void getIdByName_forNonExistentName_returnsZeroOrError() throws Exception {
        // Explanation: Verifies behavior when searching for a name that doesn't exist.
        // Room typically returns 0 for primitive int if no row is found.
        // Arrange
        Uni uni1 = createTestUni("Existing Uni", "Loc E", 2012, "exist.com", "pe");
        uniDao.insert(uni1);

        // Act
        int retrievedId = uniDao.getIdByName("NonExistent Name");

        // Assert
        assertEquals("Should return 0 for non-existent name", 0, retrievedId);
        // Note: If your method was declared to return Integer (object), you'd assertNull.
    }

    @Test
    public void getUniPasswordById_returnsCorrectPassword() throws Exception {
        // Explanation: Verifies that the correct password string is returned for a given ID.
        // *** ASSUMES Uni.java has uniPassword field mapped to uni_password column ***
        // Arrange
        String expectedPassword = "uniSecurePassword";
        Uni uni = createTestUni("Password Uni", "Loc P", 2015, "pass.edu", expectedPassword);
        long generatedId = uniDao.insert(uni);

        // Act
        String retrievedPassword = uniDao.getUniPasswordById((int)generatedId);

        // Assert
        assertNotNull(retrievedPassword);
        assertEquals("Retrieved password does not match", expectedPassword, retrievedPassword);
    }

    @Test
    public void getUniPasswordById_forNonExistentId_returnsNull() throws Exception {
        // Explanation: Verifies that null is returned when querying for the password of a non-existent ID.
        // Arrange: Insert some other uni to ensure table isn't empty
        Uni uni = createTestUni("Other Uni", "Loc O", 2016, "other.org", "pass");
        uniDao.insert(uni);
        int nonExistentId = 9999;

        // Act
        String retrievedPassword = uniDao.getUniPasswordById(nonExistentId);

        // Assert
        assertNull("Password should be null for non-existent ID", retrievedPassword);
    }
}