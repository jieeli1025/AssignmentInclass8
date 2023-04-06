package com.example.assignment_8.model;

public class MycurrentFriend {
    private static MycurrentFriend instance;
    private Friends savedData;

    private MycurrentFriend() {}

    public static MycurrentFriend getInstance() {
        if (instance == null) {
            instance = new MycurrentFriend();
        }
        return instance;
    }

    public void setSavedData(Friends data) {
        savedData = data;
    }

    public Friends getSavedData() {
        return savedData;
    }
}