package xyz.handsomelee.netty;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import xyz.handsomelee.Domain.ChatMessage;
import xyz.handsomelee.Domain.Friends;
import xyz.handsomelee.Enums.MsgActionEnum;
import xyz.handsomelee.Protocal.ChatMsg;
import xyz.handsomelee.Protocal.MessageContent;
import xyz.handsomelee.Protocal.UserChannel;
import xyz.handsomelee.Utils.JsonUtils;

import java.util.*;

public class SocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 全部的客户端通道，包括所有合法、不合法连接
    private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 全部的合法客户端通道
    public static final Map<String, UserChannel> USER_CHANNELS = new HashMap<>();

    public static Set<String> usersKeySet = USER_CHANNELS.keySet();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端所传输的消息
        String content = msg.text();
        //System.out.println("接收到的数据是：" + content);

        MessageContent messageContent = JsonUtils.jsonToPojo(content, MessageContent.class);
        Integer action = messageContent.getAction();

        usersKeySet = USER_CHANNELS.keySet();
        // 更新对应client的消息时间戳
        for (String userId : usersKeySet) {
            if(USER_CHANNELS.get(userId).getUsername().equals(messageContent.getChatMsg().getSenderId())){
                USER_CHANNELS.get(userId).setLastMessageTimeStamp(System.currentTimeMillis());
            }
        }

        if(action.equals(MsgActionEnum.CONNECT.getType())){
            // 如果是第一次连接或者重新连接
            String senderId = messageContent.getChatMsg().getSenderId();
            System.out.println(senderId);

            for (String userId : usersKeySet) {
                if(USER_CHANNELS.get(userId).getUsername().equals(messageContent.getChatMsg().getSenderId())){

                    if(messageContent.getChatMsg().getMsg().equals(USER_CHANNELS.get(userId).getToken())){
                        // 如果登录请求的token与保存的用户token完全一样，则无条件允许连接并且关闭原来的连接
                        USER_CHANNELS.get(userId).getClient().close();
                    }else {
                        // TODO 重复登陆处理
                        ChatMsg chatMsg = new ChatMsg();
                        chatMsg.setSenderId("server");
                        //chatMsg.setMsgId("2");
                        chatMsg.setMsg("User has been login, do not relogin. restart the app...");
                        chatMsg.setReceiverId(messageContent.getChatMsg().getSenderId());
                        MessageContent mc = new MessageContent();
                        mc.setChatMsg(chatMsg);
                        mc.setAction(MsgActionEnum.CHAT.getType());
                        sendToSpecificUser(messageContent.getChatMsg().getSenderId(), mc);
                        ctx.channel().close();
                        return;
                    }
                }
            }
            // 当有人上线时，向全员广播

//            ChatMsg chatMsg = new ChatMsg();
//            chatMsg.setSenderId("server");
//            //chatMsg.setMsgId("1");
//            chatMsg.setMsg(messageContent.getChatMsg().getSenderId() + "上线了！");
//            chatMsg.setReceiverId(messageContent.getChatMsg().getSenderId());
//            MessageContent mc = new MessageContent();
//            mc.setChatMsg(chatMsg);
//            mc.setAction(MsgActionEnum.CHAT.getType());


            // 将这个合法的客户端添加到userchannels中
            UserChannel userChannel = new UserChannel();
            userChannel.setClient(ctx.channel());
            userChannel.setToken(messageContent.getChatMsg().getMsg());
            userChannel.setUsername(messageContent.getChatMsg().getSenderId());
            userChannel.setClientLongId(ctx.channel().id().asLongText());
            USER_CHANNELS.put(ctx.channel().id().asLongText(), userChannel);
            System.out.println("Total User count" + USER_CHANNELS.size());

            // 检查此用户是否有未签收的消息并且发送
            checkAndSendUnsignedMessages(senderId);


        }else if(action.equals(MsgActionEnum.CHAT.getType())){
            // 发来消息
            String a_message = messageContent.getChatMsg().getMsg();

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setSenderId(messageContent.getChatMsg().getSenderId());
            chatMsg.setMsgId(messageContent.getChatMsg().getMsgId());
            chatMsg.setMsg(messageContent.getChatMsg().getMsg());
            chatMsg.setReceiverId(messageContent.getChatMsg().getSenderId());
            MessageContent mc = new MessageContent();
            mc.setChatMsg(chatMsg);
            mc.setAction(MsgActionEnum.CHAT.getType());

            // 将消息存储进入数据库中 TODO
            ChatMessage cm = new ChatMessage();
            cm.setId(messageContent.getChatMsg().getMsgId());
            cm.setMsg(messageContent.getChatMsg().getMsg());
            cm.setSendUserId(messageContent.getChatMsg().getSenderId());
            cm.setReceiveUserId(messageContent.getChatMsg().getReceiverId());
            cm.setCreateTime(Long.valueOf(messageContent.getChatMsg().getMsgId()));
            cm.setSignFlag(0);
            SocketServerInitializer.chatMessageMapper.insert(cm);


            //TODO 仅测试时保留 当客户端发送1时，自动返回连接状态，否则不会再进行重复
            if (a_message.equals("1")){
                ChatMsg _chatMsg = new ChatMsg();
                _chatMsg.setSenderId("server");
                //_chatMsg.setMsgId("1");
                _chatMsg.setMsg("[已连接]");
                _chatMsg.setReceiverId(messageContent.getChatMsg().getSenderId());
                MessageContent _mc = new MessageContent();
                _mc.setChatMsg(_chatMsg);
                _mc.setAction(MsgActionEnum.CHAT.getType());
                sendToSpecificUser(messageContent.getChatMsg().getSenderId(), _mc);
            }

            sendToSpecificUser(messageContent.getChatMsg().getReceiverId(), mc);


        }else if(action.equals(MsgActionEnum.SIGNED.getType())){
//            QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
//
//            queryWrapper.eq("sign_flag", false)
//                    .and(wrapper ->{
//                        wrapper.eq("id", messageContent.getChatMsg().getMsgId());
//                    });
            ChatMessage cm = new ChatMessage();
            cm.setId(messageContent.getChatMsg().getMsgId());
            cm.setSignFlag(1);
            SocketServerInitializer.chatMessageMapper.updateById(cm);

        }else if(action.equals(MsgActionEnum.KEEPALIVE.getType())){
            // 服务器返回心跳包
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setSenderId("server");
            chatMsg.setMsgId("");
            chatMsg.setMsg("");
            chatMsg.setReceiverId(messageContent.getChatMsg().getSenderId());
            MessageContent mc = new MessageContent();
            mc.setChatMsg(chatMsg);
            mc.setAction(MsgActionEnum.KEEPALIVE.getType());
            sendToSpecificUser(messageContent.getChatMsg().getSenderId(), mc);

        }else if(action.equals(MsgActionEnum.PULL_FRIEND.getType())){
            // 客户端发送请求好友列表 需要在getRequest中进行处理
        }
    }

    /**
     * 检查和发送用户未签收的消息
     * @param receiverUserName 接受方用户名
     */
    public static void checkAndSendUnsignedMessages(String receiverUserName){
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("sign_flag", false)
                .and(wrapper ->{
                    wrapper.eq("receive_user_id", receiverUserName);
                });
        List<ChatMessage> messages = SocketServerInitializer.chatMessageMapper.selectList(queryWrapper);
        for (ChatMessage cm: messages){
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setMsg(cm.getMsg());
            chatMsg.setSenderId(cm.getSendUserId());
            chatMsg.setReceiverId(cm.getReceiveUserId());
            chatMsg.setMsgId(cm.getId());
            MessageContent mc = new MessageContent();
            mc.setAction(MsgActionEnum.CHAT.getType());
            mc.setChatMsg(chatMsg);
            System.out.println(cm);
            sendToSpecificUser(cm.getReceiveUserId(), mc);
        }
    }

    /**
     * 向全部用户发送消息
     * @param mc 消息内容对象
     */
    public static void sendToAllUsers(MessageContent mc){
        for (String userLongId : usersKeySet) {
            USER_CHANNELS.get(userLongId).getClient().writeAndFlush(
                    new TextWebSocketFrame(JsonUtils.objectToJson(mc))
            );
        }
    }

    /**
     * 向指定用户发送消息
     * @param receiverUserName 接收方用户名
     * @param mc 消息内容对象
     */
    public static void sendToSpecificUser(String receiverUserName, MessageContent mc){
        for (String userLongId : usersKeySet) {
            if(receiverUserName.equals(USER_CHANNELS.get(userLongId).getUsername())){
                USER_CHANNELS.get(userLongId).getClient()
                        .writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(mc)));
                break;
            }
        }
    }

    /**
     * 向除了指定用户发送消息
     * @param excludeUserName 不发送到的用户名
     * @param mc 消息内容对象
     */
    public static void sendToAllUsersExcludeOne(String excludeUserName, MessageContent mc){
        for (String userLongId : usersKeySet) {
            if(excludeUserName.equals(USER_CHANNELS.get(userLongId).getUsername())){
                continue;
            }
            USER_CHANNELS.get(userLongId).getClient()
                    .writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(mc)));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
        System.out.println("NEW client login:"+ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("A client has been disconnected:"+ctx.channel().id().asLongText());
        if(USER_CHANNELS.containsKey(ctx.channel().id().asLongText())){
            usersKeySet.remove(ctx.channel().id().asLongText());
            USER_CHANNELS.remove(ctx.channel().id().asLongText());
            clients.remove(ctx.channel());
        }
        System.out.println("Total user count:" + USER_CHANNELS.size());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常后要关闭连接，同时从channel group中移除
        ctx.channel().close();
        clients.remove(ctx.channel());
    }
}
