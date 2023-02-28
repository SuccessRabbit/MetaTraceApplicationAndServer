package com.sam.metatrace.SurfaceView.Game2;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;

public class LandGameObject extends GameObject<Circle> {

    private static Canvas canvas;
    private static int maxWidth = 0;
    private static int maxHeight = 0;
    public static float radius;
    public static float marginTop = 10f;
    public static float marginSide = 40f;
    // 存储全部元素画笔
    private static final Paint[] PAINTS = new Paint[10];

    private float gt_x = 0;
    private float gt_y = 0;
    private int x = 0;
    private int y = 0;
    private int itemIndex = 0;
    private boolean isDiscovered = false;
    private Paint paint;

    public static int GOLD = 0;
    public static int WOOD = 1;
    public static int WATER = 2;
    public static int FIRE = 3;
    public static int SOIL = 4;
    public static int ROAD = 6;
    public static int PLAYER = 7;
    public static int CAN_WALK = 8;

    public float getGt_x() {
        return gt_x;
    }

    public float getGt_y() {
        return gt_y;
    }

    public void setDiscovered(boolean flag){
        isDiscovered = flag;
    }

    @Override
    public void start() {

        getTransform().setPosition(gt_x, gt_y);
        if (isDiscovered){
            setPaint(PAINTS[itemIndex]);
        }else{
            setPaint(PAINTS[5]);
        }
    }

    @Override
    public void update() {
        if (isDiscovered){
            setPaint(PAINTS[itemIndex]);
        }else{
            setPaint(PAINTS[5]);
        }

        super.paint();
    }

    public LandGameObject(int x, int y, int itemIndex){
        if (canvas == null) throw new Resources.NotFoundException("需要在构造前设置全局属性");
        this.x = x;
        this.y = y;
        this.itemIndex = itemIndex;
        // 通过整数坐标计算绝对坐标
        if (y % 2 == 0){
            // 如果是偶数行
            gt_x = marginSide + (canvas.getWidth() - 2*marginSide) / maxWidth * (x+1) - (canvas.getWidth() - 2*marginSide) / maxWidth / 2
                - radius/2.1f;

        }else{
            // 如果是奇数行
            gt_x = marginSide + (canvas.getWidth() - 2*marginSide) / maxWidth * (x+1) - (canvas.getWidth() - 2*marginSide) / maxWidth / 2
                + radius/2.1f;

        }
        gt_y = marginTop + radius + (canvas.getWidth() - 2*marginSide) / maxWidth * (y+1);
        setSprite(new Circle(radius));

    }

    public void notifyPressed(){
        this.isDiscovered = true;
    }

    public void setStatus(int itemIndex){
        this.itemIndex = itemIndex;
        setPaint(PAINTS[itemIndex]);
    }

    public static void setGlobalProperty(Canvas canvas, int maxHeight, int maxWidth){
        LandGameObject.canvas = canvas;
        LandGameObject.maxHeight = maxHeight;
        LandGameObject.maxWidth = maxWidth;

        radius = (canvas.getWidth() - 2*marginSide) / (maxWidth * 2.5f);

        // 设置全部画笔
        Paint p0 = new Paint();
        p0.setColor(Color.parseColor("#F7EE36"));
        PAINTS[0] = p0;

        Paint p1 = new Paint();
        p1.setColor(Color.parseColor("#8D8828"));
        PAINTS[1] = p1;

        Paint p2 = new Paint();
        p2.setColor(Color.parseColor("#33CDD2"));
        PAINTS[2] = p2;

        Paint p3 = new Paint();
        p3.setColor(Color.parseColor("#EA5337"));
        PAINTS[3] = p3;

        Paint p4 = new Paint();
        p4.setColor(Color.parseColor("#6A6255"));
        PAINTS[4] = p4;

        Paint p5 = new Paint();
        p5.setColor(Color.parseColor("#000000"));
        PAINTS[5] = p5;

        Paint p6 = new Paint();
        p6.setColor(Color.parseColor("#F7EE36"));
        PAINTS[6] = p6;

        Paint p7 = new Paint();
        p7.setColor(Color.parseColor("#1FFF99"));
        PAINTS[7] = p7;

        Paint p8 = new Paint();
        p8.setColor(Color.parseColor("#CBCBCB"));
        PAINTS[8] = p8;

        for (Paint paint : PAINTS) {
            if (paint != null)
                paint.setAntiAlias(true);
        }

    }

    public boolean checkIfPressed(float pointer_x, float pointer_y){
        return Math.sqrt(Math.pow((pointer_x - gt_x), 2) + Math.pow((pointer_y - gt_y), 2)) <= radius;
    }
}
