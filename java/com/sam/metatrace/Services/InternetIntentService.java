package com.sam.metatrace.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Date;

// 用于处理联网循环的背景线程
public class InternetIntentService extends IntentService {

    private static final int doFrequency = 1000 * 2;  // 线程执行频率
    private static final int sendHeartBeatFrequency = 1000 * 10; // 发送心跳包频率 必须是线程执行频率的整数倍
    private static final int maybeDisconnectedInterval = 1000 * 30; // 超过该时间间隔未收到来自服务器的任何消息，则认为已经与服务器断开连接
    private int doFlag = 0;

    public InternetIntentService(){
        super("MyIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 自动开启新的线程进行操作
        while (true) {
            try {
                Thread.sleep(doFrequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 发送心跳包
            if(doFlag % (sendHeartBeatFrequency / doFrequency) == 0){
                HttpClient.sendHeartBeat();
            }
            doFlag = (++doFlag) % 600;

            if(System.currentTimeMillis() - HttpClient.lastHeartBeatFromServer > maybeDisconnectedInterval){
                HttpClient.isConnected = false;
                // 尝试重新连接
                HttpClient.tryReconnect();
                Log.e("自定义尝试重连", "已经：" + (System.currentTimeMillis() - HttpClient.lastHeartBeatFromServer)+"ms未收到消息并且尝试重连");
            }

        }
    }
}
