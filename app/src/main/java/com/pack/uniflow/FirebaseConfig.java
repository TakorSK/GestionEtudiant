package com.pack.uniflow;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {
    private static boolean isPersistenceEnabled = false;

    public static void initialize() {
        if (!isPersistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }
    }
}
