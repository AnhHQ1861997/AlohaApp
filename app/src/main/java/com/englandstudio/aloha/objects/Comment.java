package com.englandstudio.aloha.objects;

public class Comment {
    String avatar, firstName, lastName, comment;
    long time;

    public Comment(String avatar, String firstName, String lastName, String comment, long time) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.comment = comment;
        this.time = time;
    }

    public Comment() {

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
