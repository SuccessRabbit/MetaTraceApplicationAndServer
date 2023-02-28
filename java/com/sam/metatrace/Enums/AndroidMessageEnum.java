package com.sam.metatrace.Enums;

public enum AndroidMessageEnum {
    CONNECT(1, "第一次连接或重连"),
    CHAT(2, "聊天消息"),
    GO_TO_MainMenu_FRAGMENT(3, "到达主菜单的fragment"),
    HEARTBEAT(4, "来自服务器的心跳包"),
    GO_TO_ChatRoom_FRAGMENT(5, "到达聊天室"),
    PULL_FRIENDS(6, "设置好友列表"),
    GO_TO_ADD_FRIEND_FRAGMENT(7, "跳转到添加好友fragment"),
    RECEIVE_ADD_FRIEND_RESPONSE(8, "接收到添加好友回调信息"),
    RECEIVE_REGISTER_NEW_USER_RESPONSE(9, "接收到注册新用户的回调消息"),
    MAKE_TOAST(10, "通知主线程创建一个土司消息"),
    GO_TO_GAME_1_FRAGMENT(11, "跳转到游戏1"),
    GO_TO_GAME_2_FRAGMENT(12, "跳转到游戏2");

    public final Integer type;
    public final String content;

    AndroidMessageEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
