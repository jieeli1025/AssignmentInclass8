package com.example.assignment_8.model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String message;
    private String time;
    private String sender;

    public Chat() {
    }

    public Chat(String message, String time, String sender) {
        this.message = message;
        this.time = time;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setType(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String chatText) {
        this.message = chatText;
    }

    public String toString(){
        return "Chat{" +
                "message='" + message + '\'' +
                ", type=" + time +
                ", sender='" + sender + '\'' +
                '}';
    }
}
