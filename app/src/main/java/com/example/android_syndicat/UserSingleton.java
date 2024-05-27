package com.example.android_syndicat;

import com.google.firebase.auth.FirebaseUser;

public class UserSingleton {
    private static UserSingleton instance;
    private FirebaseUser currentUser;

    private UserSingleton() {
    }

    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser user) {
        this.currentUser = user;
    }
}
