package com.sam.metatrace.SurfaceView.Game1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.sam.metatrace.Fragments.Game1Fragment;

import java.util.ArrayList;
import java.util.List;

public class GameViewControlThread implements Runnable{

    public static List<GameObject> gameObjects = new ArrayList<>();

    private SurfaceHolder surfaceHolder;
    private boolean running;
    private Canvas canvas;

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public void run() {

        surfaceHolder.addCallback(callback);

        while (running){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(surfaceHolder != null){
                synchronized (surfaceHolder){
                    canvas = surfaceHolder.lockCanvas();
                    if(canvas == null) continue;
                    // 绘制函数
                    canvas.drawColor(Color.CYAN);
                    //TODO  更新每个gameObject的绘制方法
                    for (GameObject gameObject : gameObjects) {

                        gameObject.setCanvas(canvas);
                        if (gameObject.isFirstBorn()) gameObject.start();
                        gameObject.paint();
                        gameObject.update();

                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        // 如果线程不处于运行状态
        Game1Fragment.notifySurfaceViewThreadStopped();
        gameObjects.clear();
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };
}
