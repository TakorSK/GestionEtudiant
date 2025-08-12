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

import com.pack.uniflow.Models.Section;
import com.pack.uniflow.Models.SectionDao;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.Models.UniDao;

/**
 * Instrumented test for SectionDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class SectionDaoTest {

    private UniflowDB db;
    private SectionDao sectionDao;
    private UniDao uniDao; // Needed for foreign key

    private int testUniId;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        sectionDao = db.sectionDao();
        uniDao = db.uniDao();

        // Insert prerequisite Uni
        Uni testUni = new Uni(); testUni.name = "Section Uni";
        testUniId = (int) uniDao.insert(testUni);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // Helper to create Section
    private Section createTestSection(String name, String group, int uniId) {
        Section section = new Section();
        section.name = name;
        section.groupName = group;
        section.uniId = uniId;
        section.academicYear = "2023-2024"; // Example value
        return section;
    }

    @Test
    public void insertAndGetById() throws Exception {
        // Arrange
        Section section = createTestSection("Computer Science", "CS101", testUniId);

        // Act
        sectionDao.insert(section);
        // Need to retrieve to get the auto-generated ID
        List<Section> allSections = sectionDao.getAllSections();
        int generatedId = allSections.get(0).id; // Get ID from inserted item
        Section retrievedSection = sectionDao.getById(generatedId);

        // Assert
        assertNotNull(retrievedSection);
        assertEquals(section.name, retrievedSection.name);
        assertEquals(section.groupName, retrievedSection.groupName);
        assertEquals(section.uniId, retrievedSection.uniId);
        assertEquals(generatedId, retrievedSection.id);
    }

    @Test
    public void getById_withNonExistentId_returnsNull() throws Exception {
        // Arrange (No sections inserted yet)
        // Act
        Section retrievedSection = sectionDao.getById(999); // Use an ID that won't exist
        // Assert
        assertNull(retrievedSection);
    }


    @Test
    public void insertAndGetAllSections() throws Exception {
        // Arrange
        Section section1 = createTestSection("Physics", "PHY101", testUniId);
        Section section2 = createTestSection("Mathematics", "MAT101", testUniId);

        // Act
        sectionDao.insert(section1);
        sectionDao.insert(section2);
        List<Section> allSections = sectionDao.getAllSections();

        // Assert
        assertNotNull(allSections);
        assertEquals(2, allSections.size());
    }

    @Test
    public void getAllSections_whenEmpty_returnsEmptyList() throws Exception {
        // Arrange (DB is empty for sections initially)
        // Act
        List<Section> allSections = sectionDao.getAllSections();
        // Assert
        assertNotNull(allSections);
        assertTrue(allSections.isEmpty());
    }
}