package xyz.handsomelee.Protocal;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserChannel {

    private String clientLongId;
    private Channel client;
    private String token;
    private long lastMessageTimeStamp;  // 上一次接收到用户消息的时间戳

    private String username;
    private int money;
    private int level;
}
