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
 * Instrumented test for StudentDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class StudentDaoTest {

    private UniflowDB db;
    private StudentDao studentDao;
    private UniDao uniDao; // Needed for foreign key

    private int testUniId;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        studentDao = db.studentDao();
        uniDao = db.uniDao();

        // Insert prerequisite Uni
        Uni testUni = new Uni(); testUni.name = "Student Uni";
        testUniId = (int) uniDao.insert(testUni);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // Helper using the current 7-arg constructor
    private Student createTestStudent(int id, String email, String name, int uniId) {
        return new Student(id, email, name, 21, "555-0"+id, uniId, "pass"+id);
    }

    @Test
    public void insertAndGetStudentById() throws Exception {
        // Arrange
        Student student = createTestStudent(201, "get@test.com", "Getter", testUniId);

        // Act
        studentDao.insert(student);
        Student retrieved = studentDao.getStudentById(student.id);

        // Assert
        assertNotNull(retrieved);
        assertEquals(student.email, retrieved.email);
        assertEquals(student.id, retrieved.id);
    }

    @Test
    public void getStudentById_withNonExistentId_returnsNull() throws Exception {
        // Arrange (No students inserted)
        // Act
        Student retrieved = studentDao.getStudentById(999);
        // Assert
        assertNull(retrieved);
    }

    @Test
    public void insertAndFindByEmail() throws Exception {
        // Arrange
        Student student = createTestStudent(202, "find@test.com", "Finder", testUniId);
        studentDao.insert(student);

        // Act
        Student found = studentDao.findByEmail("find@test.com");
        Student notFound = studentDao.findByEmail("nonexistent@test.com");

        // Assert
        assertNotNull(found);
        assertEquals(student.id, found.id);
        assertNull(notFound);
    }

    @Test
    public void insertMultipleAndGetAllStudents() throws Exception {
        // Arrange
        Student s1 = createTestStudent(203, "s1@test.com", "Student One", testUniId);
        Student s2 = createTestStudent(204, "s2@test.com", "Student Two", testUniId);
        studentDao.insert(s1);
        studentDao.insert(s2);

        // Act
        List<Student> allStudents = studentDao.getAllStudents();

        // Assert
        assertNotNull(allStudents);
        assertEquals(2, allStudents.size());
    }

    @Test
    public void getLatestStudent_returnsCorrectly() throws Exception {
        // Arrange
        Student s1 = createTestStudent(205, "s5@test.com", "Student Five", testUniId);
        Student s2 = createTestStudent(206, "s6@test.com", "Student Six", testUniId); // Higher ID
        studentDao.insert(s1);
        studentDao.insert(s2);

        // Act
        Student latest = studentDao.getLatestStudent();

        // Assert
        assertNotNull(latest);
        assertEquals(s2.id, latest.id); // Should be the one with the highest ID
    }

    @Test
    public void updateStudent_changesData() throws Exception {
        // Arrange
        Student student = createTestStudent(207, "update@test.com", "Updater", testUniId);
        studentDao.insert(student);
        String newName = "Updated Name";
        String newBio = "Updated Bio";

        // Act
        // Retrieve the student first to modify it
        Student toUpdate = studentDao.getStudentById(student.id);
        assertNotNull(toUpdate);
        toUpdate.fullName = newName;
        toUpdate.Bio = newBio; // Update Bio field directly
        toUpdate.isOnline = true;
        studentDao.update(toUpdate);

        // Retrieve again to check changes
        Student updated = studentDao.getStudentById(student.id);

        // Assert
        assertNotNull(updated);
        assertEquals(newName, updated.fullName);
        assertEquals(newBio, updated.Bio);
        assertTrue(updated.isOnline);
    }

    @Test
    public void setAllOffline_updatesStatus() throws Exception {
        // Arrange
        Student s1 = createTestStudent(208, "online@test.com", "Online User", testUniId);
        Student s2 = createTestStudent(209, "offline@test.com", "Offline User", testUniId);
        s1.isOnline = true; // Set one online
        studentDao.insert(s1);
        studentDao.insert(s2);

        // Act
        studentDao.setAllOffline();
        Student retrieved1 = studentDao.getStudentById(s1.id);
        Student retrieved2 = studentDao.getStudentById(s2.id);

        // Assert
        assertNotNull(retrieved1);
        assertFalse("Student 1 should be offline", retrieved1.isOnline);
        assertNotNull(retrieved2);
        assertFalse("Student 2 should be offline", retrieved2.isOnline);
    }

    @Test
    public void getOnlineStudent_returnsCorrectly() throws Exception {
        // Arrange
        Student s1 = createTestStudent(210, "online2@test.com", "Is Online", testUniId);
        Student s2 = createTestStudent(211, "offline2@test.com", "Is Offline", testUniId);
        s1.isOnline = true;
        s2.isOnline = false;
        studentDao.insert(s1);
        studentDao.insert(s2);

        // Act
        Student onlineStudent = studentDao.getOnlineStudent();

        // Assert
        assertNotNull(onlineStudent);
        assertEquals(s1.id, onlineStudent.id);
    }

    @Test
    public void getOnlineStudent_whenNoneOnline_returnsNull() throws Exception {
        // Arrange
        Student s1 = createTestStudent(212, "offline3@test.com", "Offline Three", testUniId);
        s1.isOnline = false;
        studentDao.insert(s1);

        // Act
        Student onlineStudent = studentDao.getOnlineStudent();

        // Assert
        assertNull(onlineStudent);
    }
}