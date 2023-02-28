package com.sam.metatrace.SurfaceView.Game1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sam.metatrace.MainPage;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CollisionDetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 通过额外的控制线程周期性刷新游戏示例
 */
public class PlayGround extends SurfaceView implements View.OnTouchListener {

    // 是否达成游戏目标标识
    public static boolean finishGoal = false;
    // 游戏目标
    public static int minGoal = 1000;
    public static int maxGoal = 2000;
    // 玩家游戏对象
    PlayerGameObject playerGameObject;
    // 游戏块对象
    List<BlockGameObject> blockGameObjects = new ArrayList<>();
    // UI控制管理器
    UIControllerGameObject uiControllerGameObject;

    public PlayGround(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public SurfaceHolder getSurfaceHolder(){
        return getHolder();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (finishGoal){
            if (event.getX() >= UIControllerGameObject.btn_restart_x &&
                    event.getX() <= UIControllerGameObject.btn_restart_x + UIControllerGameObject.btn_restart_width &&
            event.getY() >= UIControllerGameObject.btn_restart_y &&
            event.getY() <= uiControllerGameObject.btn_restart_y + UIControllerGameObject.btn_restart_height){
                initGame();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            // 通知playerGameObject进行移动
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            playerGameObject.moveToFingerPosition(event.getX());
        }else if (event.getAction() == MotionEvent.ACTION_UP){

        }
        return true;
    }

    public void onResume(){
        // 游戏流程初始化 每次按下返回键退出游戏并重新进入以后都会执行此函数
        initGame();

    }

    public void initGame(){
        GameViewControlThread.gameObjects.clear();
        CollisionDetection.clearAllCollisionObjects();
        Random random = new Random();
        minGoal = random.nextInt(5000) + 10;
        maxGoal = random.nextInt(1000) + minGoal;
        finishGoal = false;
        uiControllerGameObject = new UIControllerGameObject();
        playerGameObject = new PlayerGameObject();
        blockGameObjects.add(new BlockGameObject(200, 10, 0));
        blockGameObjects.add(new BlockGameObject(200, 10, 1));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                blockGameObjects.add(new BlockGameObject(200, 10, 0));
                blockGameObjects.add(new BlockGameObject(200, 10, 1));
            }
        }, 5000);

    }

}
