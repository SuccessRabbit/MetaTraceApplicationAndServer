package com.sam.metatrace.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Adapter.ChatBubbleRecyclerViewAdapter;
import com.sam.metatrace.Entity.ChatBubbleItemBean;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.Interfaces.OnDownloadFileResultListener;
import com.sam.metatrace.Interfaces.OnUploadFileResultListener;
import com.sam.metatrace.MainPage;
import com.sam.metatrace.Protocal.ChatMsg;
import com.sam.metatrace.R;
import com.sam.metatrace.Services.HttpClient;
import com.sam.metatrace.Utils.DownloadUtil;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.emoji.Emoji;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatWithOneFriendFragment extends FragmentBase {

    private View root;
    public static ChatWithOneFriendFragment instance;
    private static RecyclerView recyclerView;
    private static final List<ChatBubbleItemBean> mdatas = new ArrayList<>();
    private static String username;
    public static String intentUsername = "";
    public static EmojiPopup emojiPopup;

    public static String getHisUsername() {
        // 获取聊天窗口对方的用户名
        return username;
    }

    public void setUsername(String username) {
        ChatWithOneFriendFragment.username = username;
        txt_userName.setText(username);
    }

    private EditText input_msg;
    private ImageButton btn_send_msg;
    private ImageButton btn_send_picture;
    private ImageButton btn_back;
    private ImageButton btn_send_emoji;
    private TextView txt_userName;

    public static RecyclerView getRecyclerView(){
        return recyclerView;
    }
    public static void addItem(ChatBubbleItemBean chatBubbleItemBean){
        mdatas.add(chatBubbleItemBean);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    /**
     * 添加一个内容气泡
     * @param chatBubbleItemBean
     */
    public static void addChatBubbleItem(ChatBubbleItemBean chatBubbleItemBean){

        Date date = new Date();
        if(date.getTime() - MainPage.lastReceiveMsgTime > 1000 * 60) {
            // 如果距离上次显示消息时间戳超过60秒，则再显示一个时间戳
            ChatBubbleItemBean _chatBubbleItemBean = new ChatBubbleItemBean();
            _chatBubbleItemBean.type = 2;
            SimpleDateFormat format = new SimpleDateFormat("MM月dd日 a hh:mm:ss");
            _chatBubbleItemBean.msg = format.format(date.getTime());
            mdatas.add(_chatBubbleItemBean);
        }
        MainPage.lastReceiveMsgTime = date.getTime();
        mdatas.add(chatBubbleItemBean);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    /**
     * 清空全部聊天内容气泡
     */
    public static void clearAllChatBubbleItems(){
        mdatas.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }


    public ChatWithOneFriendFragment() {
        // Required empty public constructor
    }

    public static void hidePopup(){
        emojiPopup.dismiss();
        InputMethodManager imm = (InputMethodManager) MainPage.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View vv = instance.getActivity().getWindow().peekDecorView();
        if(vv != null)
            imm.hideSoftInputFromWindow(vv.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!intentUsername.equals("")){
            username = intentUsername;
            intentUsername = "";
        }

        txt_userName.setText(username);

        clearAllChatBubbleItems();
        List<ChatMsg> chatMsgsFromHim = MainPage.sqLiteHelper.getAllBySenderName(getHisUsername());

        for (ChatMsg chatMsg : chatMsgsFromHim) {
            // 通过数据库中的消息恢复聊天记录
            ChatBubbleItemBean chatBubbleItemBean = new ChatBubbleItemBean();
            String msg = chatMsg.getMsg();
            // 判断是否有原始文件下载链接，如果有需要尝试下载
            if(msg.startsWith("http://193.203.13.134:999/")){
                // 是图像消息
                // 下载图像
                HttpClient.downloadFile(msg, "/metaTrace", new OnDownloadFileResultListener() {
                    @Override
                    public void onDownloadSuccess(String localFilePath) {
                        getActivity().runOnUiThread(() -> {
                            ChatBubbleItemBean c = new ChatBubbleItemBean();
                            c.type = ChatBubbleRecyclerViewAdapter.IMG_NOTME;
                            c.msg = localFilePath;
                            ChatWithOneFriendFragment.addChatBubbleItem(c);
                            // 更新数据库
                            MainPage.sqLiteHelper.updateImageDatabaseMessage(msg, localFilePath);
                        });
                    }

                    @Override
                    public void onDownloadFailed() {
                    }

                    @Override
                    public void onDownloading(int progress) {
                    }
                });
                continue;
            }

            if(chatMsg.getSenderId().equals(username)){
                // 如果是对方发送的,如果对方发送的不是.jpg或者其他图片扩展名 可能会抛出异常
                chatBubbleItemBean.type = msg.contains("com.sam.metatrace/files/images/metaTrace")?
                        ChatBubbleRecyclerViewAdapter.IMG_NOTME: ChatBubbleRecyclerViewAdapter.NOT_ME;
            }else{
                // 如果是自己发送的
                chatBubbleItemBean.type = msg.contains("com.sam.metatrace/files/images/metaTrace")?
                        ChatBubbleRecyclerViewAdapter.IMG_ME: ChatBubbleRecyclerViewAdapter.ME;
            }
            chatBubbleItemBean.msg = chatMsg.getMsg();
            mdatas.add(chatBubbleItemBean);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scrollToPosition(Math.max(recyclerView.getAdapter().getItemCount() - 1, 0));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_chat_with_one_friend, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(instance == null){
            instance = this;
        }
        // 获取控件
        recyclerView = root.findViewById(R.id.recycler_view);
        input_msg = root.findViewById(R.id.input_chat);
        btn_send_msg = root.findViewById(R.id.btn_send_msg);
        btn_send_picture = root.findViewById(R.id.btn_send_picture);
        btn_back = root.findViewById(R.id.btn_chatWithFriend_back);
        txt_userName = root.findViewById(R.id.txt_chatWithFriend_name);
        btn_send_emoji = root.findViewById(R.id.btn_send_emoji);

        emojiPopup = EmojiPopup.Builder.fromRootView(
                root
        ).build(input_msg);
        btn_send_emoji.setOnClickListener(v->{
            emojiPopup.toggle();

        });

        // 准备数据
        initRecyclerViewData();

        // 设置发送按钮回调函数
        btn_send_msg.setOnClickListener(v -> {
            if(input_msg.getText().toString().equals("")){
                return;
            }
            ChatBubbleItemBean data = new ChatBubbleItemBean();
            data.msg = input_msg.getText().toString();
            input_msg.setText("");
            data.type = 0;

            addChatBubbleItem(data);

            HttpClient.sendMessage(MainPage.username, getHisUsername(), data.msg, String.valueOf(System.currentTimeMillis()));
            // 将发送出去的消息存储到本地数据库
            MainPage.sqLiteHelper.addOne(
                    String.valueOf(System.currentTimeMillis()),
                    "I"+getHisUsername(),
                    data.msg,
                    System.currentTimeMillis());
        });

        // 设置返回按钮回调函数
        btn_back.setOnClickListener(v->{
            Message message = new Message();
            message.what = AndroidMessageEnum.GO_TO_MainMenu_FRAGMENT.getType();
            mHandler.sendMessage(message);
            FragmentChat.setChatItemSignStatus(username, false);
            setUsername("");
        });

        // 设置发送图片按钮回调函数
        btn_send_picture.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 2);

        });

    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentChat.setChatItemSignStatus(username, false);
        intentUsername = username;
        setUsername("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();

                String path = getImagePath(uri, null);

                HttpClient.uploadFile("http://193.203.13.134:10101/upload", null, new File(path), new OnUploadFileResultListener() {
                    @Override
                    public void onUploadSuccess(String downloadFilePath) {
                        // 通知UI线程图像上传成功
                        Message message = new Message();
                        message.what = AndroidMessageEnum.MAKE_TOAST.getType();
                        message.obj = "图像发送成功";
                        mHandler.sendMessage(message);
                        // 向服务器发送路径消息
                        HttpClient.sendMessage(MainPage.username, getHisUsername(), downloadFilePath, String.valueOf(System.currentTimeMillis()));
                        try {
                            String localImagePath = MainPage.context.getExternalFilesDir("images")+
                            "/metaTrace/"+ DownloadUtil.getNameFromUrl(downloadFilePath);
                            // 将本地图像文件传送到应用文件夹内,并且复制的文件名称保持与发送出去的文件名称一致
                            HttpClient.copyFileUsingFileChannelsLocal(new File(path), new File(localImagePath));
                            // 保存聊天记录并且更新image view
                            MainPage.sqLiteHelper.addOne(
                                    String.valueOf(System.currentTimeMillis()),
                                    "I"+getHisUsername(),
                                    localImagePath, // 保存的是本地的图片地址
                                    System.currentTimeMillis());
                            getActivity().runOnUiThread(() -> {
                                ChatBubbleItemBean c = new ChatBubbleItemBean();
                                c.msg = localImagePath;
                                c.type = ChatBubbleRecyclerViewAdapter.IMG_ME;
                                addChatBubbleItem(c);
                            });

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUploadFailed() {
                        Message message = new Message();
                        message.what = AndroidMessageEnum.MAKE_TOAST.getType();
                        message.obj = "图像发送失败";
                        mHandler.sendMessage(message);
                    }
                });
            }
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void initRecyclerViewData(){

        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        // 创建适配器
        ChatBubbleRecyclerViewAdapter adapter = new ChatBubbleRecyclerViewAdapter(mdatas, getActivity(), getContext());
        // 设置到recyclerview 中
        recyclerView.setAdapter(adapter);
    }




}