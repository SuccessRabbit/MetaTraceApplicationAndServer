package com.sam.metatrace.Services;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.Fragments.ChatWithOneFriendFragment;
import com.sam.metatrace.Fragments.FragmentChat;
import com.sam.metatrace.Interfaces.OnDownloadFileResultListener;
import com.sam.metatrace.Interfaces.OnUploadFileResultListener;
import com.sam.metatrace.MainPage;
import com.sam.metatrace.Protocal.ChatMsg;
import com.sam.metatrace.Protocal.MessageContent;
import com.sam.metatrace.Enums.MsgActionEnum;
import com.sam.metatrace.Utils.DownloadUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.http.Multipart;

public class HttpClient {
    private static Handler handler;
    public static boolean isConnected = false;
    public static long lastHeartBeatFromServer = 0L;  // 上一次收到来自服务器的心跳包时间
    public static int unreadMessagesCount = 0;  // 未读消息个数

    public static String pull_friends_list_url_root = "http://172.18.56.160:10101/user/friends";
    public static String add_friend_url_root = "";
    public static String accept_deny_friend_url_root = "";
    public static String register_url_root = "";
    public static String login_url_root = "http://172.18.56.160:10101/user/login";
    public static String webSocket_url = "ws://172.18.56.160:9999/ws";
    public static  WebSocket webSocket;
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    /*
    设置handler
     */
    public static void setHandler(Handler hdl){
        handler = hdl;
    }
    /*
    设置url路径
     */
    public static void setUrl(String _login_root, String _webSocket_url, String _pull_friends_list_url_root, String _add_friend_url_root, String _accept_or_deny_friend_url_root,
                              String _register_url_root){
        login_url_root = _login_root;
        webSocket_url = _webSocket_url;
        pull_friends_list_url_root = _pull_friends_list_url_root;
        add_friend_url_root = _add_friend_url_root;
        accept_deny_friend_url_root = _accept_or_deny_friend_url_root;
        register_url_root = _register_url_root;
    }
    /*
    发送登录注册httpget请求
     */
    public static void sendHttpGetMethod(String username, String password){
        Request request = new Request.Builder().url(login_url_root +"?" + "username=" + username +"&" + "password="+ password).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("sendHttpGetMethod", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                String result = response.body().string();
                Log.d("sendHttpGetMethod", result);

                // 向主线程handler发送消息触发回调
                Message message = new Message();
                message.what = MsgActionEnum.CONNECT.getType();
                message.obj = result;
                handler.sendMessage(message);

                response.body().close();
            }
        });
    }

    public static void webSocketConnect(){
        Request request = new Request.Builder().url(webSocket_url).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                isConnected = false;
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.e("webSocketConnect", t.toString());
                isConnected = false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.d("Receive", text);
                isConnected = true;
                lastHeartBeatFromServer = System.currentTimeMillis();
                Gson gson = new Gson();
                Message message = new Message();
                MessageContent messageContent = gson.fromJson((String) text, MessageContent.class);
                if(messageContent.getAction().equals(MsgActionEnum.CONNECT.getType())){
                    message.what = AndroidMessageEnum.CONNECT.getType();
                }else if(messageContent.getAction().equals(MsgActionEnum.CHAT.getType())){
                    message.what = AndroidMessageEnum.CHAT.getType();
                    // 收到消息后进行向服务器进行消息签收
                    sendSignChatMessage(messageContent.getChatMsg().getMsgId());

                    // 收到消息后写入本地数据库
                    if(!messageContent.getChatMsg().getSenderId().equals("server"))
                        saveMessageToSqlite(messageContent);

                    // 创建一个notification弹出消息
                    if(ChatWithOneFriendFragment.getHisUsername()== null || ChatWithOneFriendFragment.getHisUsername() == ""){
                        NotificationService.makeOnePopUpNotification(messageContent.getChatMsg().getSenderId(),
                                messageContent.getChatMsg().getMsg());
                        if (!messageContent.getChatMsg().getSenderId().equals("server"))
                            unreadMessagesCount++;
                    }


                }else if(messageContent.getAction().equals(MsgActionEnum.SIGNED.getType())){

                }else if(messageContent.getAction().equals(MsgActionEnum.KEEPALIVE.getType())){
                    message.what = AndroidMessageEnum.HEARTBEAT.getType();
                }else if(messageContent.getAction().equals(MsgActionEnum.PULL_FRIEND.getType())){

                }

                message.obj = messageContent;
                handler.sendMessage(message);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                Log.d("webSocketConnect", "连接成功");
                // 发送登录消息包帮助服务器确认是合法的用户
                sendConnectMessage();
                sendPullFriendsListMessage();
                lastHeartBeatFromServer = System.currentTimeMillis();
                isConnected = true;

            }
        });

    }

    public static void saveMessageToSqlite(MessageContent mc){
        MainPage.sqLiteHelper.addOne(
                mc.getChatMsg().getMsgId(),
                mc.getChatMsg().getSenderId(),
                mc.getChatMsg().getMsg(),
                Long.valueOf(mc.getChatMsg().getMsgId()));

    }

    public static void tryReconnect(){
        if(webSocket != null){
            webSocket.close(1000, "closed");
        }
        webSocketConnect();
    }

    /**
     * 向服务器发送签收消息
     * @param messageId 消息id
     */
    public static void sendSignChatMessage(String messageId){
        webSocket.send(generateMessgaeJsonString(MainPage.username, "server", MainPage.token, messageId, MsgActionEnum.SIGNED));
    }

    /**
     * 发送拉取好友列表消息
     */
    public static void sendPullFriendsListMessage(){
        Request request = new Request.Builder().url(pull_friends_list_url_root +"?" + "username=" + MainPage.username).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("sendHttpGetMethod", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                String result = response.body().string();
                Log.d("sendHttpGetMethod", result);

                // 向主线程handler发送消息触发回调
                Message message = new Message();
                message.what = AndroidMessageEnum.PULL_FRIENDS.getType();
                message.obj = result;
                handler.sendMessage(message);

                response.body().close();
            }
        });
    }

    /**
     * 发送注册新用户请求
     * @param username 用户名
     * @param password 密码
     */
    public static void sendRegisterNewUserRequest(String username, String password){
        Request request = new Request.Builder().url(register_url_root +"?" +"username=" + username + "&"
                + "password=" + password).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("sendHttpGetMethod", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                String result = response.body().string();
                Log.d("sendHttpGetMethod", result);

                // 向UI线程发送回调
                Message message = new Message();
                message.what = AndroidMessageEnum.RECEIVE_REGISTER_NEW_USER_RESPONSE.getType();
                message.obj = result;
                handler.sendMessage(message);
                response.body().close();
            }
        });
    }

    /**
     * 发送同意添加或者拒绝添加好友请求
     * @param hisUsername 对方的用户名
     * @param action 行动
     */
    public static void sendAcceptOrDenyFriendRequest(String hisUsername, String action){
        Request request = new Request.Builder().url(accept_deny_friend_url_root +"?" + "action="+ action+ "&" +"myUsername=" + MainPage.username + "&"
                + "hisUsername=" + hisUsername).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("sendHttpGetMethod", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                String result = response.body().string();
                Log.d("sendHttpGetMethod", result);
                response.body().close();
            }
        });
    }

    /**
     * 发送添加好友的请求
     * @param hisUsername 接收方的用户名
     */
    public static void sendAddFriendRequest(String hisUsername){
        Request request = new Request.Builder().url(add_friend_url_root +"?" + "myUsername=" + MainPage.username + "&"
        + "hisUsername=" + hisUsername).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("sendHttpGetMethod", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                String result = response.body().string();
                Log.d("sendHttpGetMethod", result);
                // 向UI mainPage线程发送接收到的消息回调
                Message message = new Message();
                message.what = AndroidMessageEnum.RECEIVE_ADD_FRIEND_RESPONSE.getType();
                message.obj = result;
                handler.sendMessage(message);
                response.body().close();
            }
        });
    }

    /**
     * 发送登录消息包
     */
    public static void sendConnectMessage(){
        webSocket.send(generateMessgaeJsonString(MainPage.username, "server", MainPage.token, "", MsgActionEnum.CONNECT));
    }

    /**
     * 发送心跳包
     */
    public static void sendHeartBeat(){
        webSocket.send(generateMessgaeJsonString(MainPage.username, "server", "", "", MsgActionEnum.KEEPALIVE));
    }

    /**
     * 测试发送消息方法
     * @param senderNickname
     * @param message
     * @param msgId
     */
    public static void sendMessage(String senderNickname, String receiverNickName, String message, String msgId){
        webSocket.send(generateMessgaeJsonString(senderNickname, receiverNickName, message, msgId, MsgActionEnum.CHAT));
    }


    /**
     * 生成消息传输json字符串
     * @param senderId 发送者username
     * @param receiverId 接收者username
     * @param msg   消息体
     * @param msgId  消息id
     * @param action  消息类型 action type
     * @return
     */
    private static String generateMessgaeJsonString(String senderId, String receiverId, String msg, String msgId, MsgActionEnum action){
        ChatMsg chatMsg = new ChatMsg(senderId, receiverId, msg, msgId);
        MessageContent messageContent = new MessageContent(action.getType(), chatMsg);
        Gson gson = new Gson();
        return gson.toJson(messageContent);
    }

    /**
     *  上传文件函数 目前通过图像文件已经测试成功
     * @param url 上传的url路径
     * @param map 暂时无用
     * @param file 文件
     */
    public static void uploadFile(final String url, final Map<String, Object> map, File file, OnUploadFileResultListener onUploadFileResultListener) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(7000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onUploadFileResultListener.onUploadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    onUploadFileResultListener.onUploadSuccess(str);

                } else {
                    onUploadFileResultListener.onUploadFailed();
                }
            }
        });

    }

    public static void downloadFile(final String url, final String savePath, OnDownloadFileResultListener onDownloadFileResultListener){
        DownloadUtil.get().download(url, savePath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String localFilePath) {
                onDownloadFileResultListener.onDownloadSuccess(localFilePath);
                Log.e("onDownloadSuccess", "下载成功");
            }

            @Override
            public void onDownloading(int progress) {
                Log.i("下载进度 ", progress+"%");
                onDownloadFileResultListener.onDownloading(progress);
            }

            @Override
            public void onDownloadFailed() {
                Log.e("TAG", "下载失败");
                onDownloadFileResultListener.onDownloadFailed();
            }
        });
    }

    /**
     * 无需联网 本地复制文件
     * @param source 源文件
     * @param dest 目标地址
     * @throws IOException
     */
    public static void copyFileUsingFileChannelsLocal(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null)
                inputChannel.close();
            if (outputChannel != null)
                outputChannel.close();
        }
    }


    }
