package com.example.assignment_8.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Friends implements Serializable {
    private String name;
    private String email;
    private String imagetime;


    public Friends() {
    }

    public Friends(String name, String email, String imagetime) {
        this.name = name;
        this.email = email;
        this.imagetime = imagetime;

    }

    public String getImage() {
        return imagetime;
    }

    public void setImage(String imagetime) {
        this.imagetime = imagetime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString(){
        return name + " " + email + " " + imagetime + " ";
    }
}
