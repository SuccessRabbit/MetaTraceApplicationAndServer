package xyz.handsomelee.Enums;

public enum MsgActionEnum {
    CONNECT(1, "第一次连接或重连"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "心跳包"),
    PULL_FRIEND(5, "拉取好友列表");

    public final Integer type;
    public final String content;

    MsgActionEnum(Integer type, String content){
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
