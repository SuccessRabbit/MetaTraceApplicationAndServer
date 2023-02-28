package com.sam.metatrace.Protocal;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;

    @Override
    public String toString() {
        return "ChatMsg{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", msg='" + msg + '\'' +
                ", msgId='" + msgId + '\'' +
                '}';
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public ChatMsg(String senderId, String receiverId, String msg, String msgId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.msgId = msgId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
