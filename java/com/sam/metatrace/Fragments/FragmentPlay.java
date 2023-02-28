package com.sam.metatrace.Fragments;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.R;

public class FragmentPlay extends FragmentBase {
    public static FragmentPlay instance;
    public static View root;

    private Button btn_start_game_1;
    private Button btn_start_game_2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_play, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (instance == null) instance = this;

        // 绑定按钮
        btn_start_game_1 = root.findViewById(R.id.btn_start_game_demo);
        btn_start_game_1.setOnClickListener(v->{
            // 通知MainPage跳转到游戏fragment
            Message message = new Message();
            message.what = AndroidMessageEnum.GO_TO_GAME_1_FRAGMENT.getType();
            mHandler.sendMessage(message);
        });

        btn_start_game_2 = root.findViewById(R.id.btn_start_game_demo2);
        btn_start_game_2.setOnClickListener(v->{
            Message message = new Message();
            message.what = AndroidMessageEnum.GO_TO_GAME_2_FRAGMENT.getType();
            mHandler.sendMessage(message);
        });
    }
}
