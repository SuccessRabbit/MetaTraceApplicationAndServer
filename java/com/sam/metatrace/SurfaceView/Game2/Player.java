package com.sam.metatrace.SurfaceView.Game2;

import java.util.List;

public class Player {
    private static LandGameObject lastLand, nowLand;

    public static void moveToPosition(int x, int y){
        lastLand = nowLand;
        nowLand = PlayGround.getLandGameObjects()[x][y];
        nowLand.setDiscovered(true);
        nowLand.setStatus(LandGameObject.PLAYER);
        lastLand.setStatus(LandGameObject.CAN_WALK);
        List<LandGameObject> surroundLands = LandMap.getSurroundLands(x, y);
        for (LandGameObject surroundLand : surroundLands) {
            surroundLand.setDiscovered(true);
        }
    }

    public static void setInitPosition(int x, int y){
        nowLand = PlayGround.getLandGameObjects()[x][y];
        nowLand.setStatus(LandGameObject.PLAYER);
        nowLand.setDiscovered(true);
        List<LandGameObject> surroundLands = LandMap.getSurroundLands(x, y);
        for (LandGameObject surroundLand : surroundLands) {
            surroundLand.setDiscovered(true);
        }
    }
}
