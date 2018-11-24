package com.englandstudio.aloha.Objects;

public class Post {
    String avatar, firstName, lastName, status;
    long time;

    public Post(String avatar, String firstName, String lastName, String status, long time) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.time = time;
    }

    public Post() {

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
