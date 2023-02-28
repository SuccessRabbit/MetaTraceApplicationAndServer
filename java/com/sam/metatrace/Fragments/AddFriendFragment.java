package com.sam.metatrace.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.R;
import com.sam.metatrace.Services.HttpClient;

public class AddFriendFragment extends FragmentBase {

    private View root;

    private Button btn_send_add_friend;
    private EditText input_his_username;
    private ImageButton btn_back_to_mainPage;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 寻找全部需要使用的空间并且绑定对应的事件
        btn_send_add_friend = root.findViewById(R.id.btn_send_add_friend);
        btn_back_to_mainPage = root.findViewById(R.id.btn_add_friend_back);
        input_his_username = root.findViewById(R.id.input_add_friend_username);
        // 返回主菜单按钮事件绑定
        btn_back_to_mainPage.setOnClickListener(v ->{
            Message message = new Message();
            message.what = AndroidMessageEnum.GO_TO_MainMenu_FRAGMENT.getType();
            mHandler.sendMessage(message);
        });
        // 发送好友请求按钮事件绑定
        btn_send_add_friend.setOnClickListener(v->{
            String username = input_his_username.getText().toString();
            HttpClient.sendAddFriendRequest(username);
        });
    }
}