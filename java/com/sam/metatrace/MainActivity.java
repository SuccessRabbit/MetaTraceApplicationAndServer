package com.sam.metatrace;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.Services.HttpClient;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == AndroidMessageEnum.CONNECT.getType()){
                // 如果是登录http服务器回调
                String text = (String)msg.obj;
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                if(text.equals("欢迎")){
                    // 跳转页面并且建立websocket连接
                    Intent intent = new Intent(MainActivity.this, MainPage.class);
                    // nickname传值给新页面
                    intent.putExtra("username", input_username.getText().toString());
                    startActivity(intent);
                }
            }else if(msg.what == AndroidMessageEnum.RECEIVE_REGISTER_NEW_USER_RESPONSE.getType()){
                // 如果是注册消息回调
                String result = (String)msg.obj;
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Button btn_login;
    private Button btn_register;
    private EditText input_username;
    private EditText input_password;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);
        // 申请电源管理权限
        if(!isIgnoringBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }


        // 设置logo字体

        //TextView tv = findViewById(R.id.txt_login_logo);
        //Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/xingshu.TTF");
        //tv.setTypeface(tf);

        // 设置httpclient 的主线程handler
        HttpClient.setHandler(mHandler);
        // 设置httpclient 类的url信息
        HttpClient.setUrl(getString(R.string.loginoutUrlRoot), getString(R.string.webSocketUrl), getString(R.string.pull_friends_list_url_root),
                getString(R.string.add_friend_root), getString(R.string.accept_deny_friend_url_root), getString(R.string.register_url_root));
        // 获取组件
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        input_username = findViewById(R.id.ipt_username);
        input_password = findViewById(R.id.ipt_password);

        btn_register.setOnClickListener(v->{
            String usernameText = input_username.getText().toString();
            String passwordText = input_password.getText().toString();
            if(usernameText.equals("") || passwordText.equals("")){
                Toast.makeText(getApplicationContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }else{
                HttpClient.sendRegisterNewUserRequest(usernameText, passwordText);
            }
        });

        // 绑定按钮事件
        btn_login.setOnClickListener(v -> {
            String usernameText = input_username.getText().toString();
            String passwordText = input_password.getText().toString();

            HttpClient.sendHttpGetMethod(usernameText, passwordText);


        });

    }

    // 负责进行电源管理的方法，应用运行时获取常驻权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}