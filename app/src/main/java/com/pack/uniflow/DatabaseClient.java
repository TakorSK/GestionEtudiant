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
                        Executors.newSingleThreadExecutor().execute(() -> {
                            insertDefaultData(context);
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

    private void insertDefaultData(Context context) {
        try {
            // Insert Universities
            Uni nationalUni = createUniversity("National University", "Main Campus", 1950, "www.nationaluni.edu");
            long uniId1 = database.uniDao().insert(nationalUni);
            Log.d("DB_INSERT", "Inserted University ID: " + uniId1);

            Uni techUni = createUniversity("Tech Institute", "Tech Park", 1995, "www.techinstitute.edu");
            long uniId2 = database.uniDao().insert(techUni);
            Log.d("DB_INSERT", "Inserted University ID: " + uniId2);

            // Insert Clubs
            insertClub((int)uniId1, "Chess Club", "For chess enthusiasts");
            insertClub((int)uniId1, "Debate Society", "Public speaking");
            insertClub((int)uniId2, "Coding Club", "Learn programming");
            insertClub((int)uniId2, "Robotics Team", "Build robots");

        } catch (Exception e) {
            Log.e("DB_INIT", "Failed to insert default data", e);
        }
    }


    private Uni createUniversity(String name, String location, int year, String website) {
        Uni uni = new Uni();
        uni.name = name;
        uni.location = location;
        uni.establishedYear = year;
        uni.website = website;
        return uni;
    }

    private void insertClub(int uniId, String name, String description) {
        try {
            Club club = new Club();
            club.name = name;
            club.description = description;
            club.uniId = uniId;
            long clubId = database.clubDao().insert(club);
            Log.d("DB_INSERT", "Inserted Club ID: " + clubId);
        } catch (Exception e) {
            Log.e("DB_INIT", "Failed to insert club: " + name, e);
        }
    }

}