package com.sam.metatrace.SurfaceView.Game1;

import android.graphics.Color;
import android.graphics.Paint;

public class UIControllerGameObject extends GameObject {

    public static float btn_restart_x;
    public static float btn_restart_y;
    public static float btn_restart_width;
    public static float btn_restart_height;


    @Override
    public void start() {
        // 创建文字paint
        Paint customTextPaint = new Paint();
        customTextPaint.setColor(Color.DKGRAY);
        customTextPaint.setStyle(Paint.Style.FILL);
        customTextPaint.setTextSize(60);
        customTextPaint.setLetterSpacing(0.1f);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("GOAL_GUI", customTextPaint);

        customTextPaint = new Paint();
        customTextPaint.setColor(Color.parseColor("#AA8023"));
        customTextPaint.setStyle(Paint.Style.FILL);
        customTextPaint.setTextSize(100);
        customTextPaint.setLetterSpacing(0.1f);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("WIN_GUI", customTextPaint);

        customTextPaint = new Paint();
        customTextPaint.setColor(Color.WHITE);
        customTextPaint.setStyle(Paint.Style.FILL);
        customTextPaint.setTextSize(50);
        customTextPaint.setLetterSpacing(0.1f);
        customTextPaint.setTextAlign(Paint.Align.CENTER);
        addCustomPaint("BTN_GUI", customTextPaint);

        btn_restart_x = getCanvas().getWidth()/5;
        btn_restart_y = getCanvas().getHeight()/3;
        btn_restart_width = getCanvas().getWidth()*4/5 - btn_restart_x;
        btn_restart_height = getCanvas().getHeight()/2.5f - btn_restart_y;
    }

    @Override
    public void update() {
        if (PlayGround.finishGoal){
            // 如果游戏目标已经达成
            getCanvas().drawText("恭喜你，胜利！✌",
                    getCanvas().getWidth() / 2,
                    getCanvas().getHeight() / 4,
                    getCustomPaintByName("WIN_GUI"));
            getCanvas().drawRect(btn_restart_x,
                                btn_restart_y,
                    btn_restart_x + btn_restart_width,
                        btn_restart_y + btn_restart_height,
                                new Paint());
            getCanvas().drawText("点此处开始新的一局",
                    getCanvas().getWidth()/2,
                    getCanvas().getHeight()/2.7f,
                    getCustomPaintByName("BTN_GUI")
                    );
        }else {
            getCanvas().drawText("游戏目标：["+ PlayGround.minGoal+", "+ PlayGround.maxGoal+"]",
                    getCanvas().getWidth() / 2,
                    getCanvas().getHeight() / 4,
                    getCustomPaintByName("GOAL_GUI"));
        }
    }
}
