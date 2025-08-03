package com.pack.uniflow.Activities;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class UniflowApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Set persistence before any database access
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}