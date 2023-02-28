package com.sam.metatrace.SurfaceView.Game1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sam.metatrace.MainPage;
import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Box;
import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.BoxCollider;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CircleCollider;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CollisionListener;
import com.sam.metatrace.SurfaceView.SamEngine.Transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameObject<T> implements CircleCollider, BoxCollider, CollisionListener {

    // 游戏对象名称
    public String name;
    // 游戏对象画布，实际是全部的surface view
    private Canvas canvas;
    // 游戏对象的画笔
    private Paint paint;
    // 游戏对象的自定义画笔集合
    private Map<String, Paint> customPaints = new HashMap<>();
    // 该游戏对象是否是第一次初始化
    private boolean firstBorn = true;
    // 游戏对象的贴图
    private T sprite;
    // 游戏对象 transform 组件
    private Transform transform;
    // 游戏对象属性 是否可见
    private boolean isVisible = true;
    // 游戏对象是否处于碰撞状态标识
    private final Set<GameObject> isCollisions = new HashSet<>();

    /**
     * 判断游戏对象是否是第一次初始化，当调用此方法后，游戏对象的初始化标志位自动改成true
     * @return 该游戏对象是否已经初始化
     */
    public boolean isFirstBorn(){
        if (firstBorn){
            firstBorn = false;
            return true;
        }
        else return false;
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

    @Deprecated
    public Bitmap getBitmapByResId(int resId){
        return BitmapFactory.decodeResource(MainPage.context.getResources(), resId);
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

    /**
     * 获取画布
     * @return 画布
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * 无需主动调用，设置画布
     * @param canvas 画布
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * 创建该游戏对象初始化GameObject父类构造器时进行默认调用，方法体内不能包含任何canvas操作，
     * 因为此时canvas还没有被初始化
     */
    public void awake(){

    }

    /**
     * 游戏对象在第一次被循环线程setCanvas时进行默认调用，方法体内可以包含canvas操作
     */
    public void start(){

    }

    /**
     * 每一帧执行
     */
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
        // 向游戏主循环线程对象池中加入此游戏物体
        GameViewControlThread.gameObjects.add(this);
        // 初始化游戏对象transform对象
        transform = new Transform();
        // 调用游戏对象start方法
        paint = new Paint();
        awake();
    }
    public void destroy(){
        GameViewControlThread.gameObjects.remove(this);
    }

    @Override
    public float getWidth() {
        if (sprite.getClass() == Box.class)
            return ((Box)sprite).getWidth();
        else if (sprite.getClass() == Bitmap.class)
            return ((Bitmap)sprite).getWidth();
        return 1/0;
    }

    @Override
    public float getHeight() {
        if (sprite.getClass() == Box.class)
            return ((Box)sprite).getHeight();
        else if (sprite.getClass() == Bitmap.class)
            return ((Bitmap)sprite).getHeight();
        return 1/0;
    }

    @Override
    public float getRadius() {
        if (sprite.getClass() == Circle.class)
            return ((Circle)sprite).getRadius();
        else if (sprite.getClass() == Bitmap.class)
            return ((Bitmap)sprite).getHeight()/2;
        return 1/0;
    }

    @Override
    public void _onCollisionEnterOrExit(GameObject collideObject) {

    }

    @Override
    public void _onCollisionStay(GameObject collideObject) {
        if (!isCollisions.contains(collideObject)){
            onCollisionEnter(collideObject);
            isCollisions.add(collideObject);
        } else onCollisionStay(collideObject);
    }

    @Override
    public void _onNotCollision(GameObject collideObject) {
        if (isCollisions.contains(collideObject)){
            onCollisionExit(collideObject);
            isCollisions.remove(collideObject);
        }
    }

    @Override
    public float getCenterX() {
        return transform.getX();
    }

    @Override
    public float getCenterY() {
        return transform.getY();
    }

    @Override
    public int getColliderGroupId() {
        return 0;
    }

    @Override
    public Class getColliderClass() {
        return null;
    }

    @Override
    public void onCollisionEnter(GameObject collisionObject) {

    }

    @Override
    public void onCollisionStay(GameObject collisionObject) {

    }

    @Override
    public void onCollisionExit(GameObject collisionObject) {

    }
}
