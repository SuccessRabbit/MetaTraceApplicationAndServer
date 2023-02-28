package com.sam.metatrace.Protocal;

import java.io.Serializable;

public class MessageContent implements Serializable {
    private Integer action;
    private ChatMsg chatMsg;
    private String extend;

    public MessageContent(Integer action, ChatMsg chatMsg) {
        this.action = action;
        this.chatMsg = chatMsg;
    }

    public MessageContent(Integer action, ChatMsg chatMsg, String extend) {
        this.action = action;
        this.chatMsg = chatMsg;
        this.extend = extend;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
