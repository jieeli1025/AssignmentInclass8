package com.example.assignment_8.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyFirebaseFirestore {
    private static MyFirebaseFirestore instance;
    private FirebaseFirestore db;

    private MyFirebaseFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    public static MyFirebaseFirestore getInstance() {
        if (instance == null) {
            instance = new MyFirebaseFirestore();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
