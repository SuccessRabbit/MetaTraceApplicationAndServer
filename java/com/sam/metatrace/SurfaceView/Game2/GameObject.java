package com.sam.metatrace.SurfaceView.Game2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sam.metatrace.MainPage;
import com.sam.metatrace.SurfaceView.Game1.GameViewControlThread;
import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Box;
import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;
import com.sam.metatrace.SurfaceView.SamEngine.Transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameObject<T> {
    // 游戏对象名称
    public String name;
    // 游戏对象画布，实际是全部的surface view
    private Canvas canvas;
    // 游戏对象的画笔
    private Paint paint;
    // 游戏对象的自定义画笔集合
    private Map<String, Paint> customPaints = new HashMap<>();
    // 游戏对象的贴图
    private T sprite;
    // 游戏对象 transform 组件
    private Transform transform;
    // 游戏对象属性 是否可见
    private boolean isVisible = true;
    // 游戏对象是否第一次执行绘制
    private boolean isFirstBorn = true;

    /**
     * 返回该游戏对象是否第一次执行绘制，调用完成本方法后，isFirstborn标志位将会自动置为false
     * @return 是否第一次执行绘制
     */
    public boolean isFirstBorn(){
        if (isFirstBorn){
            isFirstBorn = false;
            return true;
        }else return isFirstBorn;
    }

    /**
     * 添加一个自定义paint对象到paint map中
     * @param name 名称
     * @param paint paint对象
     */
    public void addCustomPaint(String name, Paint paint){
        customPaints.put(name, paint);
    }

    /**
     * 从paint map中通过名称获取对应的paint引用
     * @param name 名称
     * @return paint引用
     */
    public Paint getCustomPaintByName(String name){
        return customPaints.get(name);
    }

    public Bitmap getBitMapByAssetName(String name){
        try {
            InputStream is = MainPage.context.getResources().getAssets().open("images/" + name);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置游戏物体 精灵（贴图）
     * @param sprite 泛型贴图
     */
    public void setSprite(T sprite) {
        this.sprite = sprite;
    }

    public T getSprite() {
        return sprite;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    /**
     * 获取画布
     * @return 画布
     */
    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void start(){

    }

    public void update(){

    }

    public final void paint(){
        if(!isVisible) return;
        if (sprite == null) return;
        if (sprite.getClass() == Bitmap.class){
            // 如果是贴图
            Bitmap bitmap = (Bitmap)sprite;
            getCanvas().drawBitmap(bitmap,
                    transform.getX() - bitmap.getWidth()/2,
                    transform.getY() - bitmap.getHeight()/2,
                    getPaint());
        }else if(sprite.getClass() == Box.class){
            Box box = (Box)sprite;
            getCanvas().drawRect(transform.getX() - box.getWidth()/2,
                    transform.getY() - box.getHeight()/2,
                    transform.getX() + box.getWidth()/2,
                    transform.getY() + box.getHeight()/2,
                    getPaint());
        }else if(sprite.getClass() == Circle.class){
            Circle circle = (Circle)sprite;
            RectF rectF = new RectF(transform.getX()-circle.getRadius(),
                    transform.getY()-circle.getRadius(),
                    transform.getX()+circle.getRadius(),
                    transform.getY()+circle.getRadius());
            getCanvas().drawArc(rectF, 0, 360, true, paint);
        }
    }

    public Transform getTransform(){
        return this.transform;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public GameObject() {
        name = System.currentTimeMillis()+"";
        // 初始化游戏对象transform对象
        transform = new Transform();
        // 调用游戏对象start方法
        paint = new Paint();
    }
    public void destroy(){

    }
}
