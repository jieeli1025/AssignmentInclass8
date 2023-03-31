package com.example.assignment_8.model;

import java.io.Serializable;

public class Chat implements Serializable {
    private String message;
    private int type;
    private String sender;

    public Chat() {
    }

    public Chat(String message, int type, String sender) {
        this.message = message;
        this.type = type;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
                ", type=" + type +
                ", sender='" + sender + '\'' +
                '}';
    }
}
