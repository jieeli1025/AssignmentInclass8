package com.example.assignment_8.model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String message;
    private String image;
    private String sender;
    private String email;

    private int type;

    public Chat() {
    }

    public Chat(String message, String time, String sender, int type, String email) {
        this.message = message;
        this.image = image;
        this.sender = sender;
        this.type = type;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getimage() {
        return image;
    }

    public void setimage(String image) {
        this.image = image;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String chatText) {
        this.message = chatText;
    }

    public String toString(){
        return message + " " + image + " " + sender + " " + type + " " + email + " ";
    }
}
