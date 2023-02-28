package com.sam.metatrace.SurfaceView.Game2;


import android.graphics.Canvas;

import com.sam.metatrace.SurfaceView.SamEngine.BasicShapes.Circle;

import java.util.ArrayList;
import java.util.List;

public class GameManager{

    // 存储全部游戏对象的列表
    private final List<GameObject> gameObjects = new ArrayList<>();

    private Canvas canvas;

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * 绘制全部游戏对象列表中的游戏对象，需要再调用该方法之前使用setCanvas方法对canvas进行设置
     */
    public void paintAllGameObjects(){
        for (GameObject gameObject : gameObjects) {

            gameObject.setCanvas(canvas);
            if (gameObject.isFirstBorn()) gameObject.start();
            gameObject.paint();
            gameObject.update();
        }
    }

    /**
     * 向游戏对象列表中增加一个游戏对象
     * @param gameObject 增加的游戏对象
     */
    public void addGameObject(GameObject gameObject){
        gameObjects.add(gameObject);
    }

    /**
     * 从游戏对象列表中移除一个游戏对象
     * @param gameObject 需要移除的游戏对象
     */
    public void removeGameObject(GameObject gameObject){
        gameObjects.remove(gameObject);
    }

    public GameManager(){

    }
}
