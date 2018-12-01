package com.englandstudio.aloha.objects;

public class Friend {
    String avatar, firstName, lastName, title;

    public Friend(String avatar, String firstName, String lastName, String title) {
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
    }

    public Friend() {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
