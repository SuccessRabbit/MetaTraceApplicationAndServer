package com.sam.metatrace.SurfaceView.Game2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Box;
import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 通过主动刷新游戏画面的游戏示例
 */
public class PlayGround extends SurfaceView implements View.OnTouchListener {

    private GameManager gameManager;
    private Canvas canvas;
    private List<SignGameObject> signGameObjectList = new ArrayList<>();
    private static final int maxWidth = 10;
    private static final int maxHeight = 17;

    public static LandGameObject[][] getLandGameObjects() {
        return landGameObjects;
    }

    private static final LandGameObject[][] landGameObjects = new LandGameObject[maxWidth][maxHeight];

    // 颜色常量
    final int C_GOLD = Color.parseColor("#F7EE36");
    final int C_WOOD = Color.parseColor("#8D8828");
    final int C_WATER = Color.parseColor("#33CDD2");
    final int C_FIRE = Color.parseColor("#EA5337");
    final int C_SOIL = Color.parseColor("#6A6255");

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            // 创建游戏管理者物体
            gameManager = new GameManager();
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(C_GOLD);

            canvas = holder.lockCanvas();

            // 创建 金木水火土 的元素指示符
            // 金
            SignGameObject signGameObject = new SignGameObject(
                    canvas.getWidth()/5 - canvas.getWidth()/10.5f,
                    canvas.getHeight()-canvas.getWidth()/9,
                    paint,
                    SignGameObject.GOLD
                    );
            signGameObjectList.add(signGameObject);
            // 木
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(C_WOOD);
            signGameObject = new SignGameObject(
                    canvas.getWidth()*2/5 - canvas.getWidth()/10.5f,
                    canvas.getHeight()-canvas.getWidth()/9,
                    paint,
                    SignGameObject.WOOD
            );
            signGameObjectList.add(signGameObject);
            // 水
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(C_WATER);
            signGameObject = new SignGameObject(
                    canvas.getWidth()*3/5 - canvas.getWidth()/10.5f,
                    canvas.getHeight()-canvas.getWidth()/9,
                    paint,
                    SignGameObject.WATER
            );
            signGameObjectList.add(signGameObject);
            // 火
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(C_FIRE);
            signGameObject = new SignGameObject(
                    canvas.getWidth()*4/5 - canvas.getWidth()/10.5f,
                    canvas.getHeight()-canvas.getWidth()/9,
                    paint,
                    SignGameObject.FIRE
            );
            signGameObjectList.add(signGameObject);
            // 土
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(C_SOIL);
            signGameObject = new SignGameObject(
                    canvas.getWidth() - canvas.getWidth()/10.5f,
                    canvas.getHeight()-canvas.getWidth()/9,
                    paint,
                    SignGameObject.SOIL
            );
            signGameObjectList.add(signGameObject);

            for (SignGameObject gameObject : signGameObjectList) {
                gameManager.addGameObject(gameObject);
            }

            // 创建land
            LandGameObject.setGlobalProperty(canvas, maxHeight, maxWidth);
            for (int i = 0; i < maxWidth*maxHeight; i++) {
                LandGameObject landGameObject = new LandGameObject(i%maxWidth, i/maxWidth, LandGameObject.GOLD);
                gameManager.addGameObject(landGameObject);
                landGameObjects[i%maxWidth][i/maxWidth] = landGameObject;
            }

            // 随机分配玩家初始位置
            Random random = new Random();
            int init_x = random.nextInt(maxWidth);
            int init_y = random.nextInt(maxHeight);
            Player.setInitPosition(init_x, init_y);
            landGameObjects[init_x][init_y].setDiscovered(true);

            holder.unlockCanvasAndPost(canvas);
            // 更新画布
            updateCanvas();
        }
        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }
        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    };
    public PlayGround(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    /**
     * 更新游戏画面
     */
    public void updateCanvas(){
        SurfaceHolder surfaceHolder = getHolder();
        synchronized (surfaceHolder) {
            canvas = surfaceHolder.lockCanvas();
            // 绘制函数
            canvas.drawColor(Color.parseColor("#251E06"));
            gameManager.setCanvas(canvas);
            gameManager.paintAllGameObjects();
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public SurfaceHolder getSurfaceHolder(){
        return getHolder();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            // 检测是否点击到land对象
            notifyPressedLandGameObject(event.getX(), event.getY());


        }else if (event.getAction() == MotionEvent.ACTION_MOVE){

        }else if (event.getAction() == MotionEvent.ACTION_UP){
            //更新游戏画布
            updateCanvas();
        }
        return true;
    }

    public void onResume(){
        // 游戏流程初始化 每次按下返回键退出游戏并重新进入以后都会执行此函数
        initGame();
    }

    public void initGame() {
        getHolder().addCallback(callback);
    }

    public void notifyPressedLandGameObject(float pointer_x, float pointer_y){
        float delta_y = landGameObjects[0][1].getGt_y() - landGameObjects[0][0].getGt_y();
        int row = (int) Math.min(
                Math.max((pointer_y - landGameObjects[0][0].getGt_y() + LandGameObject.radius), 0) / (delta_y),
                maxHeight-1);
        for (int i = 0; i < maxWidth; i++) {
            if (landGameObjects[i][row].checkIfPressed(pointer_x, pointer_y)){
                // 如果被点击
                //landGameObjects[i][row].notifyPressed();
                Player.moveToPosition(i, row);
            }
        }
    }

}
