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
 * Instrumented test for ClubDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class ClubDaoTest {

    private UniflowDB db;
    private ClubDao clubDao;
    private UniDao uniDao; // Needed for foreign key constraint

    private int testUniId; // Store the ID of the Uni for FK reference

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        clubDao = db.clubDao();
        uniDao = db.uniDao();

        // Explanation: Insert a prerequisite Uni for the foreign key.
        Uni testUni = new Uni();
        testUni.name = "Prereq Uni";
        testUniId = (int) uniDao.insert(testUni); // Store the generated ID
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetAllClubs() throws Exception {
        // Explanation: Tests inserting a Club (linked to the prerequisite Uni)
        // and retrieving it via getAllClubs.
        // Arrange: Create a Club object linked to the test Uni ID.
        Club club = new Club();
        club.name = "Test Club";
        club.description = "A club for testing";
        club.uniId = testUniId; // Use the ID of the inserted Uni

        // Act: Insert the club and then get all clubs.
        long generatedId = clubDao.insert(club);
        List<Club> allClubs = clubDao.getAllClubs();

        // Assert: Check that the list is not empty, contains one item,
        // and the retrieved club's details match.
        assertFalse(allClubs.isEmpty());
        assertEquals(1, allClubs.size());
        assertEquals(club.name, allClubs.get(0).name);
        assertEquals(club.uniId, allClubs.get(0).uniId);
        assertEquals((int) generatedId, allClubs.get(0).id);
    }

    @Test
    public void getAllClubsWhenEmpty() throws Exception {
        // Explanation: Tests retrieving all Clubs when the table is empty.
        // Arrange: (Database Club table is empty after setup)
        // Act: Get all Clubs.
        List<Club> allClubs = clubDao.getAllClubs();
        // Assert: The returned list should be empty.
        assertTrue(allClubs.isEmpty());
    }
}