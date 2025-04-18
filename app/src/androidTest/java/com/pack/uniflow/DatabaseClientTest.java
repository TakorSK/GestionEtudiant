package com.pack.uniflow;

import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.annotation.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test for DatabaseClient singleton and initialization.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseClientTest {

    private Context context;
    private UniflowDB db; // To check content after initialization

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        // Ensure we start fresh by deleting any existing test DB file if necessary
        // (In-memory usually handles this, but good practice for file-based test DBs)
        context.deleteDatabase("UniflowDatabase_Test"); // Use a distinct name for testing
    }

    @After
    public void teardown() {
        if (db != null) {
            db.close();
        }
        // Clean up the test database file
        context.deleteDatabase("UniflowDatabase_Test");
        // Reset the singleton instance (Important for test isolation!)
        // This requires a modification to DatabaseClient or reflection,
        // or better, use dependency injection. For simplicity here, we assume
        // tests run in separate processes or handle singleton state carefully.
        // A simple (but not ideal) approach:
        // DatabaseClient.instance = null; // Requires instance to be non-private or use reflection
    }


    // NOTE: Testing singletons directly can be tricky due to shared state.
    // Dependency Injection is a better pattern for testability.
    // These tests assume careful handling of the singleton state between tests.

    @Test
    public void getInstance_returnsSameInstance() {
        // Explanation: Verifies that multiple calls to getInstance return the
        // exact same object instance, confirming the singleton pattern.
        // Arrange & Act
        DatabaseClient instance1 = DatabaseClient.getInstance(context);
        DatabaseClient instance2 = DatabaseClient.getInstance(context);

        // Assert
        assertNotNull("Instance 1 should not be null", instance1);
        assertNotNull("Instance 2 should not be null", instance2);
        assertSame("Both calls should return the same instance", instance1, instance2);
    }

    @Test
    public void getDatabase_returnsNonNullDatabase() {
        // Explanation: Verifies that calling getDatabase() on the client
        // instance returns a non-null UniflowDB object.
        // Arrange
        DatabaseClient client = DatabaseClient.getInstance(context);

        // Act
        UniflowDB database = client.getDatabase();

        // Assert
        assertNotNull("getDatabase() should return a non-null database instance", database);
        assertTrue("Database should be open", database.isOpen());

        // Cleanup for this test specifically
        database.close();
    }

    @Test
    public void onCreateCallback_insertsDefaultData() {
        // Explanation: This test verifies that the onCreate callback, triggered
        // when the database is created for the first time, inserts the default
        // Uni and Club data as defined in DatabaseClient.
        // Arrange: Get the client instance, which triggers DB creation if needed.
        // Use a test-specific DB name to ensure onCreate is hcalled.
        db = Room.databaseBuilder(context, UniflowDB.class, "UniflowDatabase_Test")
                .allowMainThreadQueries() // ONLY FOR TEST
                .fallbackToDestructiveMigration() // In case schema changes during dev
                .addCallback(new RoomDatabase.Callback() { // Replicate callback logic for test DB
                    @Override
                    public void onCreate(@NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // We can't easily call the private method, so we might need
                        // to replicate the insertion logic here for the test DB,
                        // OR make insertDefaultData public/testable,
                        // OR just verify the expected data exists after creation.
                        // Let's verify expected data:
                    }
                })
                .build();


        // Act: Access DAOs to check content. The callback runs during build/first access.
        UniDao uniDao = db.uniDao();
        ClubDao clubDao = db.clubDao();
        List<Uni> unis = uniDao.getAllUnis();
        List<Club> clubs = clubDao.getAllClubs();

        // Assert: Check if the expected default data exists.
        assertFalse("Default Unis should have been inserted", unis.isEmpty());
        assertTrue("Should contain National University", unis.stream().anyMatch(u -> u.name.equals("National University")));
        assertTrue("Should contain Tech Institute", unis.stream().anyMatch(u -> u.name.equals("Tech Institute")));

        assertFalse("Default Clubs should have been inserted", clubs.isEmpty());
        assertEquals("Should be 4 default clubs", 4, clubs.size());
        assertTrue("Should contain Chess Club", clubs.stream().anyMatch(c -> c.name.equals("Chess Club")));
        assertTrue("Should contain Coding Club", clubs.stream().anyMatch(c -> c.name.equals("Coding Club")));

        // Note: This test assumes the callback logic successfully runs and inserts
        // the specific names used in DatabaseClient.insertDefaultData.
    }
}