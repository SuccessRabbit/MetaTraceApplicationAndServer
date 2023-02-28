package com.sam.metatrace.SurfaceView.Game2;

import android.graphics.Color;
import android.graphics.Paint;

import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;
import com.sam.metatrace.SurfaceView.SamEngine.Transform;

public class SignGameObject extends GameObject<Circle> {

    public static final String GOLD = "金";
    public static final String WOOD = "木";
    public static final String WATER = "水";
    public static final String FIRE = "火";
    public static final String SOIL = "土";


    private Paint paint;
    private float x;
    private float y;
    private int number = 0;
    private String itemToShow;

    @Override
    public void update() {
        getCanvas().drawText(number+"", getTransform().getX(), getTransform().getY(), getCustomPaintByName("TEXT"));
        getCanvas().drawText(itemToShow, getTransform().getX(), getTransform().getY()+80, getCustomPaintByName("TEXT"));
    }

    /**
     * 修改元素数量
     * @param delta 改变的数量
     */
    public void addItemNumber(int delta){
        this.number += delta;
    }

    /**
     * 金木水火土元素标志物
     * @param x 标志物横坐标
     * @param y 标志物纵坐标
     * @param _paint 画笔
     * @param itemToShow 标志物属性
     */
    public SignGameObject(float x, float y, Paint _paint, String itemToShow){
        // 增加ui画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("TEXT", paint);

        this.paint = _paint;
        this.x = x;
        this.y = y;
        this.itemToShow = itemToShow;
    }

    @Override
    public void start() {
        setSprite(new Circle(getCanvas().getWidth()/10.5f));
        setPaint(this.paint);
        getTransform().setPosition(x, y);
    }
}
