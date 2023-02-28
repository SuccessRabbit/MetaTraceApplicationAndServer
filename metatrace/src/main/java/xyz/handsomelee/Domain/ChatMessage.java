package xyz.handsomelee.Domain;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String id;
    private String sendUserId;
    private String receiveUserId;
    private String msg;
    private int signFlag;
    private Long createTime;
}
