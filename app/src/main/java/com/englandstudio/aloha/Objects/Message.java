package com.englandstudio.aloha.Objects;

public class Message {
    String avatar, firstName, lastName, message;
    long time;

    public Message(String avatar, String firstName, String lastName, String message, long time) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
        this.time = time;
    }

    public Message() {

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
