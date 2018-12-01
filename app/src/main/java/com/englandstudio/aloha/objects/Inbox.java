package com.englandstudio.aloha.objects;

public class Inbox {
    String avatar, message;
    long time;

    public Inbox(String avatar, String message, long time) {
        this.avatar = avatar;
        this.message = message;
        this.time = time;
    }

    public Inbox() {

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
