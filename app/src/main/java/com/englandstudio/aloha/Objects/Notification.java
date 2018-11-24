package com.englandstudio.aloha.Objects;

public class Notification {
    String avatar, firstName, lastName, comment, type;
    long time;

    public Notification(String avatar, String firstName, String lastName, String comment,String type, long time) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.comment = comment;
        this.type = type;
        this.time = time;
    }

    public Notification() {

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
