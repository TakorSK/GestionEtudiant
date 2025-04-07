package com.pack.uniflow;

import android.content.Context;
import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private UniflowDB database;

    private DatabaseClient(Context context) {
        database = Room.databaseBuilder(
                        context.getApplicationContext(),
                        UniflowDB.class,
                        "UniflowDatabase" // database file name
                ).fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public UniflowDB getDatabase() {
        return database;
    }
}
