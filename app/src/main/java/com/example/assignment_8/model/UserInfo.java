package com.example.assignment_8.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInfo implements Parcelable {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public static Bitmap imageBitmap = null;
    private Friends friend;


    public UserInfo() {
    }

    public UserInfo(FirebaseAuth mAuth, FirebaseUser currentUser, Friends friend) {
        this.mAuth = mAuth;
        this.currentUser = currentUser;
        this.friend = friend;
    }

    protected UserInfo(Parcel in) {
        currentUser = in.readParcelable(FirebaseUser.class.getClassLoader());
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }


    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public Friends getFriend() {
        return friend;
    }

    public void setFriend(Friends friend) {
        this.friend = friend;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable((Parcelable) this.mAuth, flags);
        dest.writeParcelable(this.currentUser, flags);
        dest.writeParcelable((Parcelable) this.friend, flags);

    }
}
