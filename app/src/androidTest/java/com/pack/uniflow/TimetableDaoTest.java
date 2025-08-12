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
 * Instrumented test for TimetableDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class TimetableDaoTest {

    private UniflowDB db;
    private TimetableDao timetableDao;
    private SectionDao sectionDao; // Needed for foreign key
    private UniDao uniDao;         // Needed for Section prerequisite

    private int testSectionId;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        timetableDao = db.timetableDao();
        sectionDao = db.sectionDao();
        uniDao = db.uniDao();

        // Insert prerequisite Uni and Section
        Uni testUni = new Uni(); testUni.name = "Timetable Uni";
        int uniId = (int) uniDao.insert(testUni);

        Section testSection = new Section();
        testSection.name = "Timetable Section";
        testSection.groupName = "TT101";
        testSection.uniId = uniId;
        sectionDao.insert(testSection);
        // Retrieve the section to get its auto-generated ID
        List<Section> sections = sectionDao.getAllSections();
        testSectionId = sections.get(0).id;
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // Helper to create Timetable
    private Timetable createTestTimetable(int sectionId) {
        Timetable tt = new Timetable();
        tt.sectionId = sectionId;
        // Add other fields if your Timetable model has them
        // tt.dayOfWeek = "Monday";
        // tt.startTime = "09:00";
        return tt;
    }

    @Test
    public void insertAndGetAllTimetables() throws Exception {
        // Arrange
        Timetable tt1 = createTestTimetable(testSectionId);
        Timetable tt2 = createTestTimetable(testSectionId); // Another entry for the same section

        // Act
        timetableDao.insert(tt1);
        timetableDao.insert(tt2);
        List<Timetable> allTimetables = timetableDao.getAllTimetables();

        // Assert
        assertNotNull(allTimetables);
        assertEquals(2, allTimetables.size());
        // Add more specific assertions if Timetable has more fields
        assertEquals(testSectionId, allTimetables.get(0).sectionId);
        assertEquals(testSectionId, allTimetables.get(1).sectionId);
    }

    @Test
    public void getAllTimetables_whenEmpty_returnsEmptyList() throws Exception {
        // Arrange (DB is empty for timetables initially)
        // Act
        List<Timetable> allTimetables = timetableDao.getAllTimetables();
        // Assert
        assertNotNull(allTimetables);
        assertTrue(allTimetables.isEmpty());
    }
}