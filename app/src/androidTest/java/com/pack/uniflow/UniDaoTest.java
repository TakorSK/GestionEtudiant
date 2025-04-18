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

/**
 * Instrumented test for UniDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class UniDaoTest {

    private UniflowDB db;
    private UniDao uniDao;

    @Before
    public void createDb() {
        // Explanation: Setup runs before each test.
        // Get context from the instrumentation registry.
        Context context = ApplicationProvider.getApplicationContext();
        // Create an in-memory database (data is lost when process dies).
        // Allow main thread queries ONLY for testing convenience.
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        // Get the DAO instance from the database.
        uniDao = db.uniDao();
    }

    @After
    public void closeDb() throws IOException {
        // Explanation: Teardown runs after each test.
        // Close the database connection.
        db.close();
    }

    @Test
    public void insertAndGetUniById() throws Exception {
        // Explanation: Tests inserting a Uni object and retrieving it by its ID.
        // Arrange: Create a Uni object.
        Uni uni = new Uni();
        uni.name = "Test University";
        uni.location = "Test Location";
        uni.establishedYear = 2000;
        uni.website = "test.com";

        // Act: Insert the Uni and get its generated ID. Retrieve by ID.
        long generatedId = uniDao.insert(uni);
        Uni retrievedUni = uniDao.getById((int) generatedId);

        // Assert: Check if retrieved object is not null and its name matches.
        assertNotNull(retrievedUni);
        assertEquals(uni.name, retrievedUni.name);
        assertEquals((int) generatedId, retrievedUni.id); // Verify ID matches
    }

    @Test
    public void insertAndFindByName() throws Exception {
        // Explanation: Tests inserting a Uni and finding it by its name.
        // Arrange: Create and insert a Uni.
        Uni uni = new Uni();
        uni.name = "FindMe University";
        uni.location = "Somewhere";
        uniDao.insert(uni);

        // Act: Retrieve the Uni by its name.
        Uni foundUni = uniDao.findByName("FindMe University");

        // Assert: Check if the found object is not null and the name matches.
        assertNotNull(foundUni);
        assertEquals(uni.name, foundUni.name);
    }

    @Test
    public void getAllUnisWhenEmpty() throws Exception {
        // Explanation: Tests retrieving all Unis when the table is empty.
        // Arrange: (Database is empty after setup)
        // Act: Get all Unis.
        List<Uni> allUnis = uniDao.getAllUnis();
        // Assert: The returned list should be empty.
        assertTrue(allUnis.isEmpty());
    }

    @Test
    public void insertMultipleAndGetAllUnis() throws Exception {
        // Explanation: Tests inserting multiple Unis and retrieving all of them.
        // Arrange: Create and insert two Uni objects.
        Uni uni1 = new Uni(); uni1.name = "Uni One";
        Uni uni2 = new Uni(); uni2.name = "Uni Two";
        uniDao.insert(uni1);
        uniDao.insert(uni2);

        // Act: Get all Unis.
        List<Uni> allUnis = uniDao.getAllUnis();

        // Assert: Check if the list contains exactly two items.
        assertEquals(2, allUnis.size());
    }
}