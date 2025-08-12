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

import com.pack.uniflow.Models.Student;
import com.pack.uniflow.Models.StudentDao;
import com.pack.uniflow.Models.Uni;
import com.pack.uniflow.Models.UniDao;

/**
 * Instrumented test for StudentDao interface.
 */
@RunWith(AndroidJUnit4.class)
public class StudentDaoTest {

    private UniflowDB db;
    private StudentDao studentDao;
    private UniDao uniDao; // Needed for foreign key

    private long testUniId1; // Store ID for first Uni
    private long testUniId2; // Store ID for second Uni

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UniflowDB.class)
                .allowMainThreadQueries()
                .build();
        studentDao = db.studentDao();
        uniDao = db.uniDao();

        db.clearAllTables(); // Clear before each test

        // Insert prerequisite Unis
        Uni uni1 = new Uni(); uni1.name = "Student Uni 1";
        testUniId1 = uniDao.insert(uni1);

        Uni uni2 = new Uni(); uni2.name = "Student Uni 2";
        testUniId2 = uniDao.insert(uni2);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // Helper using the current 7-arg constructor
    private Student createTestStudent(int id, String email, String name, int uniId) {
        // Use the correct uniId passed to the helper
        return new Student(id, email, name, 21, "555-0"+id, uniId, "pass"+id);
    }

    // --- Existing Tests (Keep These) ---

    @Test
    public void insertAndGetStudentById() throws Exception {
        Student student = createTestStudent(201, "get@test.com", "Getter", (int)testUniId1);
        studentDao.insert(student);
        Student retrieved = studentDao.getStudentById(student.id);
        assertNotNull(retrieved);
        assertEquals(student.email, retrieved.email);
        assertEquals(student.id, retrieved.id);
    }

    @Test
    public void getStudentById_withNonExistentId_returnsNull() throws Exception {
        Student retrieved = studentDao.getStudentById(999);
        assertNull(retrieved);
    }

    @Test
    public void insertAndFindByEmail() throws Exception {
        Student student = createTestStudent(202, "find@test.com", "Finder", (int)testUniId1);
        studentDao.insert(student);
        Student found = studentDao.findByEmail("find@test.com");
        Student notFound = studentDao.findByEmail("nonexistent@test.com");
        assertNotNull(found);
        assertEquals(student.id, found.id);
        assertNull(notFound);
    }

    @Test
    public void insertMultipleAndGetAllStudents() throws Exception {
        Student s1 = createTestStudent(203, "s1@test.com", "Student One", (int)testUniId1);
        Student s2 = createTestStudent(204, "s2@test.com", "Student Two", (int)testUniId2); // Different Uni
        studentDao.insert(s1);
        studentDao.insert(s2);
        List<Student> allStudents = studentDao.getAllStudents();
        assertNotNull(allStudents);
        assertEquals(2, allStudents.size());
    }

    @Test
    public void getLatestStudent_returnsCorrectly() throws Exception {
        Student s1 = createTestStudent(205, "s5@test.com", "Student Five", (int)testUniId1);
        Student s2 = createTestStudent(206, "s6@test.com", "Student Six", (int)testUniId1); // Higher ID
        studentDao.insert(s1);
        studentDao.insert(s2);
        Student latest = studentDao.getLatestStudent();
        assertNotNull(latest);
        assertEquals(s2.id, latest.id);
    }

    @Test
    public void updateStudent_changesData() throws Exception {
        Student student = createTestStudent(207, "update@test.com", "Updater", (int)testUniId1);
        studentDao.insert(student);
        String newName = "Updated Name";
        String newBio = "Updated Bio";
        Student toUpdate = studentDao.getStudentById(student.id);
        assertNotNull(toUpdate);
        toUpdate.fullName = newName;
        toUpdate.Bio = newBio;
        toUpdate.isOnline = true;
        studentDao.update(toUpdate);
        Student updated = studentDao.getStudentById(student.id);
        assertNotNull(updated);
        assertEquals(newName, updated.fullName);
        assertEquals(newBio, updated.Bio);
        assertTrue(updated.isOnline);
    }

    @Test
    public void setAllOffline_updatesStatus() throws Exception {
        Student s1 = createTestStudent(208, "online@test.com", "Online User", (int)testUniId1);
        Student s2 = createTestStudent(209, "offline@test.com", "Offline User", (int)testUniId1);
        s1.isOnline = true;
        studentDao.insert(s1);
        studentDao.insert(s2);
        studentDao.setAllOffline();
        Student retrieved1 = studentDao.getStudentById(s1.id);
        Student retrieved2 = studentDao.getStudentById(s2.id);
        assertNotNull(retrieved1);
        assertFalse("Student 1 should be offline", retrieved1.isOnline);
        assertNotNull(retrieved2);
        assertFalse("Student 2 should be offline", retrieved2.isOnline);
    }

    @Test
    public void getOnlineStudent_returnsCorrectly() throws Exception {
        Student s1 = createTestStudent(210, "online2@test.com", "Is Online", (int)testUniId1);
        Student s2 = createTestStudent(211, "offline2@test.com", "Is Offline", (int)testUniId1);
        s1.isOnline = true;
        s2.isOnline = false;
        studentDao.insert(s1);
        studentDao.insert(s2);
        Student onlineStudent = studentDao.getOnlineStudent();
        assertNotNull(onlineStudent);
        assertEquals(s1.id, onlineStudent.id);
    }

    @Test
    public void getOnlineStudent_whenNoneOnline_returnsNull() throws Exception {
        Student s1 = createTestStudent(212, "offline3@test.com", "Offline Three", (int)testUniId1);
        s1.isOnline = false;
        studentDao.insert(s1);
        Student onlineStudent = studentDao.getOnlineStudent();
        assertNull(onlineStudent);
    }

    // --- NEW TESTS for getStudentsByUniId ---

    @Test
    public void getStudentsByUniId_returnsCorrectStudents() throws Exception {
        // Explanation: Verifies that only students belonging to the specified Uni ID are returned.
        // Arrange: Insert students into different Unis
        Student s_uni1_1 = createTestStudent(401, "s_uni1_1@test.com", "Uni1 Student1", (int)testUniId1);
        Student s_uni1_2 = createTestStudent(402, "s_uni1_2@test.com", "Uni1 Student2", (int)testUniId1);
        Student s_uni2_1 = createTestStudent(403, "s_uni2_1@test.com", "Uni2 Student1", (int)testUniId2);
        studentDao.insert(s_uni1_1);
        studentDao.insert(s_uni1_2);
        studentDao.insert(s_uni2_1);

        // Act: Get students for Uni 1
        List<Student> uni1Students = studentDao.getStudentsByUniId((int)testUniId1);

        // Assert: Check results for Uni 1
        assertNotNull(uni1Students);
        assertEquals("Should retrieve 2 students for Uni 1", 2, uni1Students.size());
        // Check if the correct students are present (order might not be guaranteed by query)
        assertTrue("List should contain student s_uni1_1", uni1Students.stream().anyMatch(s -> s.id == s_uni1_1.id));
        assertTrue("List should contain student s_uni1_2", uni1Students.stream().anyMatch(s -> s.id == s_uni1_2.id));
        assertFalse("List should NOT contain student s_uni2_1", uni1Students.stream().anyMatch(s -> s.id == s_uni2_1.id));

        // Act: Get students for Uni 2
        List<Student> uni2Students = studentDao.getStudentsByUniId((int)testUniId2);

        // Assert: Check results for Uni 2
        assertNotNull(uni2Students);
        assertEquals("Should retrieve 1 student for Uni 2", 1, uni2Students.size());
        assertEquals("Student ID should match s_uni2_1", s_uni2_1.id, uni2Students.get(0).id);
    }

    @Test
    public void getStudentsByUniId_returnsEmptyListForNonExistentUniId() throws Exception {
        // Explanation: Verifies an empty list is returned when querying for a Uni ID that doesn't exist.
        // Arrange: Insert a student for a known Uni
        Student s_uni1_1 = createTestStudent(404, "s_uni1_4@test.com", "Uni1 Student4", (int)testUniId1);
        studentDao.insert(s_uni1_1);
        int nonExistentUniId = 9999; // An ID not inserted in setUp

        // Act: Get students for the non-existent Uni ID
        List<Student> students = studentDao.getStudentsByUniId(nonExistentUniId);

        // Assert: Check the list is empty
        assertNotNull(students);
        assertTrue("List should be empty for non-existent Uni ID", students.isEmpty());
    }

    @Test
    public void getStudentsByUniId_returnsEmptyListWhenNoStudentsForUni() throws Exception {
        // Explanation: Verifies an empty list is returned for a valid Uni ID that has no associated students.
        // Arrange: Uni 1 and Uni 2 exist from setUp, but insert a student only for Uni 1
        Student s_uni1_1 = createTestStudent(405, "s_uni1_5@test.com", "Uni1 Student5", (int)testUniId1);
        studentDao.insert(s_uni1_1);

        // Act: Get students for Uni 2 (which has no students inserted in this test)
        List<Student> uni2Students = studentDao.getStudentsByUniId((int)testUniId2);

        // Assert: Check the list is empty
        assertNotNull(uni2Students);
        assertTrue("List should be empty for Uni with no students", uni2Students.isEmpty());
    }
}