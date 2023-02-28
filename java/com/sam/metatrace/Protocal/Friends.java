package com.sam.metatrace.Protocal;

import java.io.Serializable;

public class Friends implements Serializable {
    private String senderId;
    private String receiverId;
    private boolean isAccepted;

    @Override
    public String toString() {
        return "Friends{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", isAccepted=" + isAccepted +
                '}';
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public Friends() {
    }

    public Friends(String senderId, String receiverId, boolean isAccepted) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isAccepted = isAccepted;
    }
}
