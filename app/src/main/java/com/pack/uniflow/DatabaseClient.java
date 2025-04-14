package com.pack.uniflow;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

public class DatabaseClient {
    private static DatabaseClient instance;
    private UniflowDB database;
    private static final Object LOCK = new Object();

    private DatabaseClient(Context context) {
        database = Room.databaseBuilder(
                        context.getApplicationContext(),
                        UniflowDB.class,
                        "UniflowDatabase"
                )
                .fallbackToDestructiveMigration()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            insertDefaultData(DatabaseClient.this.database);
                        });
                    }
                })
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DatabaseClient(context);
                }
            }
        }
        return instance;
    }

    public UniflowDB getDatabase() {
        return database;
    }

    private void insertDefaultData(UniflowDB database) {
        try {
            // Insert Universities
            Uni nationalUni = new Uni();
            nationalUni.name = "National University";
            nationalUni.location = "Main Campus";
            nationalUni.establishedYear = 1950;
            nationalUni.website = "www.nationaluni.edu";
            long uniId1 = database.uniDao().insert(nationalUni);
            Log.d("DB_INSERT", "Inserted University with ID: " + uniId1);

            Uni techUni = new Uni();
            techUni.name = "Tech Institute";
            techUni.location = "Tech Park";
            techUni.establishedYear = 1995;
            techUni.website = "www.techinstitute.edu";
            long uniId2 = database.uniDao().insert(techUni);
            Log.d("DB_INSERT", "Inserted University with ID: " + uniId2);

            // Insert Clubs
            insertClub(database, "Chess Club", "For chess enthusiasts", (int)uniId1);
            insertClub(database, "Debate Society", "Public speaking", (int)uniId1);
            insertClub(database, "Coding Club", "Learn programming", (int)uniId2);
            insertClub(database, "Robotics Team", "Build robots", (int)uniId2);

        } catch (Exception e) {
            Log.e("DB_INIT", "Failed to insert default data", e);
        }
    }

    private void insertClub(UniflowDB database, String name, String description, int uniId) {
        try {
            Club club = new Club();
            club.name = name;
            club.description = description;
            club.uniId = uniId;
            long clubId = database.clubDao().insert(club);
            Log.d("DB_INSERT", "Inserted Club: " + name + " with ID: " + clubId);
        } catch (Exception e) {
            Log.e("DB_INIT", "Failed to insert club: " + name, e);
        }
    }
}