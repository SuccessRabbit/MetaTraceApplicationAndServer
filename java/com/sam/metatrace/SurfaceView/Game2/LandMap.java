package com.sam.metatrace.SurfaceView.Game2;

import java.util.ArrayList;
import java.util.List;

public class LandMap {

    /**
     * 获取全部邻接地块
     * @param x 中心地块x坐标
     * @param y 中心地块y坐标
     * @return 全部临界地块
     */
    public static List<LandGameObject> getSurroundLands(int x, int y){
        List<LandGameObject> result = new ArrayList<>();
        LandGameObject[][] landGameObjects = PlayGround.getLandGameObjects();

        if (x-1 >= 0) result.add(landGameObjects[x-1][y]);
        if (x+1 < PlayGround.getLandGameObjects().length) result.add(landGameObjects[x+1][y]);
        if (y % 2 == 0){
            // 如果是偶数行
            if (y-1 >= 0){
                result.add(landGameObjects[x][y-1]);
                if (x-1 >= 0)
                    result.add(landGameObjects[x-1][y-1]);
            }
            if (y+1 < landGameObjects[0].length){
                result.add(landGameObjects[x][y+1]);
                if (x-1 >= 0)
                    result.add(landGameObjects[x-1][y+1]);
            }
        }else{
            // 如果是奇数行
            if (y-1 >= 0){
                result.add(landGameObjects[x][y-1]);
                if (x+1 < landGameObjects.length)
                    result.add(landGameObjects[x+1][y-1]);
            }
            if (y+1 < landGameObjects[0].length){
                result.add(landGameObjects[x][y+1]);
                if (x+1 < landGameObjects.length)
                    result.add(landGameObjects[x+1][y+1]);
            }
        }

        return result;
    }
}
