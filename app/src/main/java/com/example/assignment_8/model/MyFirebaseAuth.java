package com.example.assignment_8.model;

import com.google.firebase.auth.FirebaseAuth;

public class MyFirebaseAuth {
    private static MyFirebaseAuth instance;
    private FirebaseAuth mAuth;

    private MyFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static MyFirebaseAuth getInstance() {
        if (instance == null) {
            instance = new MyFirebaseAuth();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }
}