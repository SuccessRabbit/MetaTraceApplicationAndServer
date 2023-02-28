package com.sam.metatrace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Adapter.ChatBubbleRecyclerViewAdapter;
import com.sam.metatrace.Database.SQLiteHelper;
import com.sam.metatrace.Entity.ChatBubbleItemBean;
import com.sam.metatrace.Entity.FriendListViewItemBean;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.Fragments.AddFriendFragment;
import com.sam.metatrace.Fragments.ChatWithOneFriendFragment;
import com.sam.metatrace.Enums.MsgActionEnum;
import com.sam.metatrace.Fragments.FragmentChat;
import com.sam.metatrace.Fragments.FragmentFriend;
import com.sam.metatrace.Fragments.Game1Fragment;
import com.sam.metatrace.Fragments.Game2Fragment;
import com.sam.metatrace.Fragments.MainMenuFragment;
import com.sam.metatrace.Interfaces.OnDownloadFileResultListener;
import com.sam.metatrace.Protocal.ChatMsg;
import com.sam.metatrace.Protocal.Friends;
import com.sam.metatrace.Protocal.MessageContent;
import com.sam.metatrace.Services.HttpClient;
import com.sam.metatrace.Services.InternetIntentService;
import com.sam.metatrace.Services.NotificationService;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity  {

    public static String username = "";
    public static MainPage context;
    public static SQLiteHelper sqLiteHelper;

    // 上次显示消息的时间 用于生成聊天界面的时间显示
    public static long lastReceiveMsgTime = 0;
    // 记录本客户端的登录token，服务器端只要确认此token，则无条件接受来自本客户端的重连请求
    public static String token;

    public static FragmentBase chatWithOneFriendFragment = new ChatWithOneFriendFragment();
    public static FragmentBase mainMenuFragment = new MainMenuFragment();
    public static FragmentBase addFriendFragment = new AddFriendFragment();
    public static FragmentBase game1Fragment = new Game1Fragment();
    public static FragmentBase game2Fragment = new Game2Fragment();

    private final Handler mHandler = new Handler(Looper.myLooper()){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == AndroidMessageEnum.CONNECT.getType()){

            }else if(msg.what == AndroidMessageEnum.CHAT.getType()){
                // 通知FragmentChat更新消息快照
                FragmentChat.updateChatListViewItemByFinalChatLastMsg(((MessageContent)msg.obj).getChatMsg().getSenderId());
                String message = ((MessageContent)msg.obj).getChatMsg().getMsg();

                // 如果此时不在发来消息的好友的聊天界面，则不产生聊天气泡
                if(!((MessageContent)msg.obj).getChatMsg().getSenderId().equals(ChatWithOneFriendFragment.getHisUsername())) {
                    // 检查消息是否是图像信息，如果是则自动下载图像并且将聊天记录数据库中的对应消息链接换为下载完成的本地图像链接
                    if(message.startsWith("http://193.203.13.134:999/")){
                        // 是图像消息
                        // 下载图像
                        HttpClient.downloadFile(message, "/metaTrace", new OnDownloadFileResultListener() {
                            @Override
                            public void onDownloadSuccess(String localFilePath) {
                                // 更新数据库
                                sqLiteHelper.updateImageDatabaseMessage(message, localFilePath);
                            }
                            @Override
                            public void onDownloadFailed() {
                            }
                            @Override
                            public void onDownloading(int progress) {
                            }
                        });
                    }

                    return;
                }
                ChatBubbleItemBean chatBubbleItemBean;
                chatBubbleItemBean = new ChatBubbleItemBean();

                if(message.startsWith("http://193.203.13.134:999/")){
                    // 是图像消息
                    // 下载图像
                    HttpClient.downloadFile(message, "/metaTrace", new OnDownloadFileResultListener() {
                        @Override
                        public void onDownloadSuccess(String localFilePath) {
                            runOnUiThread(() -> {
                                ChatBubbleItemBean c = new ChatBubbleItemBean();
                                c.type = ChatBubbleRecyclerViewAdapter.IMG_NOTME;
                                c.msg = localFilePath;
                                ChatWithOneFriendFragment.addChatBubbleItem(c);
                                // 更新数据库
                                sqLiteHelper.updateImageDatabaseMessage(message, localFilePath);
                            });
                        }
                        @Override
                        public void onDownloadFailed() {
                        }
                        @Override
                        public void onDownloading(int progress) {
                        }
                    });
                }
                else {
                    chatBubbleItemBean.type = ChatBubbleRecyclerViewAdapter.NOT_ME;
                    chatBubbleItemBean.msg = message;
                    ChatWithOneFriendFragment.addChatBubbleItem(chatBubbleItemBean);
                }
            } else if(msg.what == AndroidMessageEnum.GO_TO_MainMenu_FRAGMENT.getType()){
                FragmentChat.setChatItemSignStatus(ChatWithOneFriendFragment.getHisUsername(), false);
                replaceFragment(mainMenuFragment);
            } else if(msg.what == AndroidMessageEnum.GO_TO_ChatRoom_FRAGMENT.getType()){
                ((ChatWithOneFriendFragment)chatWithOneFriendFragment).setUsername((String)msg.obj);
                replaceFragment(chatWithOneFriendFragment);
            } else if(msg.what == AndroidMessageEnum.GO_TO_ADD_FRIEND_FRAGMENT.getType()){
                replaceFragment(addFriendFragment);
            } else if(msg.what == AndroidMessageEnum.RECEIVE_ADD_FRIEND_RESPONSE.getType()){
                Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
            } else if(msg.what == AndroidMessageEnum.PULL_FRIENDS.getType()){
                Log.d("MainPage线程获取到好友列表", "handleMessage: "+msg.obj);
                Gson gson = new Gson();
                List<Friends> fromJson = gson.fromJson((String) msg.obj, new TypeToken<List<Friends>>(){}.getType());
                List<FriendListViewItemBean> data = new ArrayList<>();
                for (Friends friend : fromJson) {
                    // 如果是发送好友请求的一方，不显示在好友列表中
                    if(!friend.isAccepted() && friend.getSenderId().equals(username)) continue;
                    // 否则显示
                    String friend_username = friend.getSenderId().equals(username)? friend.getReceiverId(): friend.getSenderId();
                    FriendListViewItemBean friendListViewItemBean = new FriendListViewItemBean();
                    friendListViewItemBean.who = friend_username;
                    friendListViewItemBean.isAccepted = friend.isAccepted();
                    friendListViewItemBean.icon = friend.isAccepted()? R.drawable.icon_smile_face: R.drawable.icon_sad_face;
                    data.add(friendListViewItemBean);
                }
                FragmentFriend.getFragmentFriendListAdapter().setData(data);
            } else if(msg.what == AndroidMessageEnum.MAKE_TOAST.getType()){
                Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
            } else if(msg.what == AndroidMessageEnum.GO_TO_GAME_1_FRAGMENT.getType()){
                replaceFragment(game1Fragment);
            } else if (msg.what == AndroidMessageEnum.GO_TO_GAME_2_FRAGMENT.getType()){
                replaceFragment(game2Fragment);
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        EmojiManager.install(new GoogleEmojiProvider());
        token = Long.toString(System.currentTimeMillis());
        setContentView(R.layout.activity_main_page);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.parseColor("#CACACA"));
//        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 初始化main menu fragment
        replaceFragment(chatWithOneFriendFragment);
        replaceFragment(mainMenuFragment);


        HttpClient.setHandler(mHandler);
        // 连接webSocket
        HttpClient.webSocketConnect();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        // 创建Notification 前台常驻服务
        intent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        // 创建后台 intent service服务
        intent = new Intent(this, InternetIntentService.class);
        startService(intent);


        sqLiteHelper = new SQLiteHelper(this, 2);
        // TODO 测试数据库相关方法
        // sqLiteHelper.deleteAll(); // 删除所有聊天记录
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            setContentView(R.layout.activity_main_page);
            super.onBackPressed();
            return;
        }
        if((System.currentTimeMillis() - exitTime) > 2000){
            Toast.makeText(getApplicationContext(), "再按一下返回到桌面",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            Intent i = new Intent(Intent.ACTION_MAIN);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.addCategory(Intent.CATEGORY_HOME);
//            startActivity(i);
            moveTaskToBack(true);
        }
    }
    private void replaceFragment(FragmentBase fragment){
        FragmentBase.setmHandler(mHandler);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContiainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpClient.unreadMessagesCount = 0;

    }
}