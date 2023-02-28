package com.sam.metatrace.SurfaceView.Game1;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CircleCollider;
import com.sam.metatrace.SurfaceView.SamEngine.Collisions.CollisionDetection;

public class PlayerGameObject extends GameObject<Bitmap>{

    private static int Y_POSITION;
    private float score = 10;

    @SuppressLint("DefaultLocale")
    @Override
    public void update() {
        getCanvas().drawText(String.format("%.1f", score), getTransform().getX(), getTransform().getY()-40, getCustomPaintByName("SCORE_HUD"));
        if (score >= PlayGround.minGoal && score <= PlayGround.maxGoal) PlayGround.finishGoal = true;
    }

    @Override
    public void start() {
        setSprite(getBitMapByAssetName("1.png"));
        name = "player";
        Y_POSITION = (int) (0.9 * getCanvas().getHeight());
        getTransform().setPosition(getSprite().getHeight()/2, Y_POSITION);
        CollisionDetection.setCollisionObject(this);

        // 创建文字paint
        Paint customTextPaint = new Paint();
        customTextPaint.setColor(Color.RED);
        customTextPaint.setStyle(Paint.Style.FILL);
        customTextPaint.setTextSize(100);
        customTextPaint.setLetterSpacing(0.1f);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("SCORE_HUD", customTextPaint);

    }

    public void moveToFingerPosition(float x){
        getTransform().setPosition(x, Y_POSITION);
    }

    @Override
    public void onCollisionEnter(GameObject collisionObject) {
        if (collisionObject.name.equals("block")){
            // 通过块属性进行运算 得到更新之后的分数
            BlockGameObject blockGameObject = (BlockGameObject)collisionObject;
            if(blockGameObject.getBlockProperty().isAlive())
                score = blockGameObject.getBlockProperty().executeCalculation(score);

            Matrix matrix = new Matrix();
            matrix.preScale((float)Math.log10(10+score/10), (float)Math.log10(10+score/10));
            Bitmap bitmap = getBitMapByAssetName("1.png");
            setSprite(Bitmap.createBitmap(bitmap
                    , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));
        }

    }

    @Override
    public void onCollisionExit(GameObject collisionObject) {

    }

    @Override
    public Class getColliderClass() {
        return CircleCollider.class;
    }
}
