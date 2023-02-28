package com.sam.metatrace.SurfaceView.Game1;

import android.graphics.Color;
import android.graphics.Paint;

import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Box;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.BoxCollider;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CollisionDetection;

import java.util.Random;

public class BlockGameObject extends GameObject<Box>{

    float yPos = -100;
    float height = 200;
    float speed = 5;  // pixel per frame
    float xPos = 500;
    int posIdx = 0;

    private BlockProperty mBlockProperty;

    public BlockProperty getBlockProperty() {
        return mBlockProperty;
    }

    /**
     * 构造方法
     * @param height 块高度
     * @param speed 块移动速度
     * @param posIdx 块所属列
     */
    public BlockGameObject(float height, float speed, int posIdx){
        this.height = height;
        this.speed = speed;
        this.yPos = -height/2;
        this.posIdx = posIdx;
        mBlockProperty = new BlockProperty();
    }

    @Override
    public void start() {
        name = "block";
        setSprite(new Box(0, (int)height));
        getTransform().setPosition(0, yPos);
        getPaint().setColor(Color.parseColor("#45a02313"));
        getSprite().setWidth(getCanvas().getWidth()/2);
        if (this.posIdx == 1) this.xPos = 3*getSprite().getWidth()/2;
        else this.xPos = getSprite().getWidth()/2;
        CollisionDetection.setCollisionObject(this);

        // 创建文字paint
        Paint customTextPaint = new Paint();
        customTextPaint.setColor(Color.BLUE);
        customTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        customTextPaint.setStrokeWidth(2);
        customTextPaint.setTextSize(80);
        customTextPaint.setLetterSpacing(0.1f);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("CALCULATE_HUD", customTextPaint);
    }

    @Override
    public void update() {
        getCanvas().drawText(mBlockProperty.getDescription(), getTransform().getX(), getTransform().getY(), getCustomPaintByName("CALCULATE_HUD"));
        if (yPos < getCanvas().getHeight() + height/2){
            yPos += speed;
        }else{
            // 如果该块已经离开屏幕下边缘
            resetBlock();
        }
        getTransform().setPosition(xPos, yPos);
    }

    private void resetBlock(){
        yPos = -height/2;
        mBlockProperty.resetProperty();
    }

    @Override
    public Class getColliderClass() {
        return BoxCollider.class;
    }
}

/**
 * 块属性类，提供相关的运算方法
 */
class BlockProperty{
    public static final int TYPE_ADD = 0;
    public static final int TYPE_MINUS = 1;
    public static final int TYPE_MULTIPLY = 2;
    public static final int TYPE_DIVIDE = 3;

    private int type;
    private float number;
    private Random random = new Random();
    private boolean alive = true;

    /**
     * 获取块属性运算操作描述字符串
     * @return 描述字符串
     */
    public String getDescription(){
        StringBuilder sb = new StringBuilder();
        switch (type){
            case TYPE_ADD:
                sb.append("+");
                break;
            case TYPE_MINUS:
                sb.append("-");
                break;
            case TYPE_MULTIPLY:
                sb.append("×");
                break;
            case TYPE_DIVIDE:
                sb.append("÷");
                break;
        }
        sb.append((int)number);
        return sb.toString();
    }

    /**
     * 计算经过该块之后的数
     * @param raw_number 原始数
     * @return 运算之后的数
     */
    public float executeCalculation(float raw_number){
        alive = false;
        switch (type){
            case TYPE_ADD:
                return raw_number + number;
            case TYPE_MINUS:
                return raw_number - number;
            case TYPE_MULTIPLY:
                return raw_number * number;
            case TYPE_DIVIDE:
                return raw_number / number;
        }
        return raw_number;
    }

    public boolean isAlive() {
        return alive;
    }

    public BlockProperty(){
        type = random.nextInt(4);
        number = random.nextInt(10)+1;
    }

    public void resetProperty(){
        type = random.nextInt(4);
        number = random.nextInt(10)+1;
        alive = true;
    }
}
