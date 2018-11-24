package com.englandstudio.aloha.Objects;

public class FriendRequest {
    String avatar, firstName, lastName, from;

    public FriendRequest(String avatar, String firstName, String lastName, String from) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.from = from;
    }

    public FriendRequest() {

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
