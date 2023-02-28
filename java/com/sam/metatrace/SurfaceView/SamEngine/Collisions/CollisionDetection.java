package com.sam.metatrace.SurfaceView.SamEngine.Collisions;

import android.util.Log;

import com.sam.metatrace.SurfaceView.Game1.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollisionDetection implements Runnable{

    private static Map<Integer, List<BaseCollider>> collisionGroups = new HashMap<>();
    public static boolean running= true;


    public static void setCollisionObject(BaseCollider baseColliderObject){

        if (collisionGroups.get(baseColliderObject.getColliderGroupId()) == null){
            List<BaseCollider> arr = new ArrayList<>();
            arr.add(baseColliderObject);
            collisionGroups.put(baseColliderObject.getColliderGroupId(), arr);
        }else{
            collisionGroups.get(baseColliderObject.getColliderGroupId()).add(baseColliderObject);
        }
        Log.e("碰撞组信息", "碰撞组 "+ baseColliderObject.getColliderGroupId() +" 物体数量："+ collisionGroups.get(baseColliderObject.getColliderGroupId()).size());
    }

    public static void removeCollisionObject(BaseCollider baseColliderObject){
        collisionGroups.get(baseColliderObject.getColliderGroupId()).remove(baseColliderObject);
        Log.e("碰撞组信息", "碰撞组 "+ baseColliderObject.getColliderGroupId() +" 物体数量："+ collisionGroups.get(baseColliderObject.getColliderGroupId()).size());
    }

    public static void clearAllCollisionObjects(){
        collisionGroups.clear();
    }

    @Override
    public void run() {

        while(running){
            Set<Integer> keys = collisionGroups.keySet();
            for (Integer collisionGroupId : keys) {
                List<BaseCollider> baseColliders = collisionGroups.get(collisionGroupId);

                for (int i = 0; i < baseColliders.size(); i++) {
                    for (int j = i+1; j < baseColliders.size(); j++) {
                        BaseCollider colliderA = baseColliders.get(i);
                        BaseCollider colliderB = baseColliders.get(j);
                        if (colliderA.getColliderClass() == CircleCollider.class &&
                        colliderB.getColliderClass() == CircleCollider.class){
                            // 如果两个碰撞体都是圆形碰撞器
                            float thresh = ((CircleCollider)colliderA).getRadius() +
                                    ((CircleCollider)colliderB).getRadius();
                            float distance = getCenterDistance(colliderA, colliderB);
                            if((int)(distance-thresh) == 0 || (int)Math.abs(distance-thresh) == 1){
                                colliderA._onCollisionEnterOrExit((GameObject)colliderB);
                                colliderB._onCollisionEnterOrExit((GameObject)colliderA);
                            }else if(distance <= thresh){
                                colliderA._onCollisionStay((GameObject)colliderB);
                                colliderB._onCollisionStay((GameObject)colliderA);
                            }
                        } else if ((colliderA.getColliderClass() == CircleCollider.class && colliderB.getColliderClass() == BoxCollider.class)
                        || (colliderA.getColliderClass() == BoxCollider.class && colliderB.getColliderClass() == CircleCollider.class)){
                            // 如果两个碰撞体一个是圆一个是矩形
                            CircleCollider circle = colliderA.getColliderClass() == CircleCollider.class? (CircleCollider)colliderA: (CircleCollider)colliderB;
                            BoxCollider box = circle == colliderA? (BoxCollider)colliderB: (BoxCollider)colliderA;
                            if ((int)getCircleToRectDistance(box, circle) == circle.getRadius()
                                    || (int)Math.abs(getCircleToRectDistance(box, circle)-circle.getRadius())==1 ){
                                colliderA._onCollisionEnterOrExit((GameObject)colliderB);
                                colliderB._onCollisionEnterOrExit((GameObject)colliderA);
                            }else if (getCircleToRectDistance(box, circle) < circle.getRadius()){
                                colliderA._onCollisionStay((GameObject)colliderB);
                                colliderB._onCollisionStay((GameObject)colliderA);
                            }else{
                                colliderA._onNotCollision((GameObject)colliderB);
                                colliderB._onNotCollision((GameObject)colliderA);
                            }

                        } else if (colliderA.getColliderClass() == BoxCollider.class && colliderB.getColliderClass() == BoxCollider.class){
                            // 如果两个碰撞体都是矩形碰撞体
                            float[][] boxA = new float[4][2];
                            float[][] boxB = new float[4][2];
                            boxA[0][0] = colliderA.getCenterX() - ((BoxCollider)colliderA).getWidth() / 2;
                            boxA[0][1] = colliderA.getCenterY() - ((BoxCollider)colliderA).getHeight() / 2;
                            boxA[1][0] = colliderA.getCenterX() + ((BoxCollider)colliderA).getWidth() / 2;
                            boxA[1][1] = colliderA.getCenterY() - ((BoxCollider)colliderA).getHeight() / 2;
                            boxA[2][0] = colliderA.getCenterX() + ((BoxCollider)colliderA).getWidth() / 2;
                            boxA[2][1] = colliderA.getCenterY() + ((BoxCollider)colliderA).getHeight() / 2;
                            boxA[3][0] = colliderA.getCenterX() - ((BoxCollider)colliderA).getWidth() / 2;
                            boxA[3][1] = colliderA.getCenterY() + ((BoxCollider)colliderA).getHeight() / 2;
                            boxB[0][0] = colliderB.getCenterX() - ((BoxCollider)colliderB).getWidth() / 2;
                            boxB[0][1] = colliderB.getCenterY() - ((BoxCollider)colliderB).getHeight() / 2;
                            boxB[1][0] = colliderB.getCenterX() + ((BoxCollider)colliderB).getWidth() / 2;
                            boxB[1][1] = colliderB.getCenterY() - ((BoxCollider)colliderB).getHeight() / 2;
                            boxB[2][0] = colliderB.getCenterX() + ((BoxCollider)colliderB).getWidth() / 2;
                            boxB[2][1] = colliderB.getCenterY() + ((BoxCollider)colliderB).getHeight() / 2;
                            boxB[3][0] = colliderB.getCenterX() - ((BoxCollider)colliderB).getWidth() / 2;
                            boxB[3][1] = colliderB.getCenterY() + ((BoxCollider)colliderB).getHeight() / 2;
                            if (boxA[2][0] < boxB[0][0] || boxA[0][0] > boxB[2][0] || boxA[2][1] < boxB[0][1] || boxA[0][1] > boxB[2][1]){
                                colliderA._onNotCollision((GameObject)colliderB);
                                colliderB._onNotCollision((GameObject)colliderA);
                            }else if (checkIfRectContact(boxA, boxB)){
                                colliderA._onCollisionEnterOrExit((GameObject)colliderB);
                                colliderB._onCollisionEnterOrExit((GameObject)colliderA);
                            }else{
                                colliderA._onCollisionStay((GameObject)colliderB);
                                colliderB._onCollisionStay((GameObject)colliderA);
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        collisionGroups.clear();
    }

    /**
     * 获取两个物体中心点之间的距离
     * @param A 物体A
     * @param B 物体B
     * @return 距离
     */
    public float getCenterDistance(BaseCollider A, BaseCollider B){

        return Math.round(Math.sqrt(Math.pow((A.getCenterX() - B.getCenterX()), 2)
                + Math.pow((A.getCenterY() - B.getCenterY()), 2)));
    }

    /**
     * 检测两个矩形是否相互接触但不重叠
     * @param boxA 矩形碰撞器1
     * @param boxB 矩形碰撞器2
     * @return 是否接触而不重叠
     */
    public boolean checkIfRectContact(float[][] boxA, float[][] boxB){
        for (int i = 0; i < boxA.length; i++) {
            for (int j = 0; j < boxB.length; j++) {
                if (boxA[i][0] == boxA[(i+1)%boxA.length][0]){
                    // 如果是A矩形的两个点的横坐标相同
                    if (boxB[j][0] == boxA[i][0] && boxB[j][1] >= Math.min(boxA[i][1], boxA[(i+1)%boxA.length][1])
                            && boxB[j][1] <= Math.max(boxA[i][1], boxA[(i+1)%boxA.length][1])) return true;
                }else if (boxA[i][1] == boxA[(i+1)%boxA.length][1]){
                    // 如果是A矩形的两个点的纵坐标相同
                    if (boxB[j][1] == boxA[i][1] && boxB[j][0] >= Math.min(boxA[i][0], boxA[(i+1)%boxA.length][0])
                            && boxB[j][0] <= Math.max(boxA[i][0], boxA[(i+1)%boxA.length][0])) return true;
                }
            }
        }

        return false;
    }

    /**
     * 计算圆形碰撞器到矩形碰撞器的最短距离
     * @param box 矩形碰撞器
     * @param circle 圆形碰撞器
     * @return 最短距离
     */
    public float getCircleToRectDistance(BoxCollider box, CircleCollider circle){
        float distance = 0, closestX, closestY;

        if (circle.getCenterX() <= box.getCenterX()-box.getWidth()/2){
            closestX = box.getCenterX()-box.getWidth()/2;
        }else if (circle.getCenterX() >= box.getCenterX() + box.getWidth()/2){
            closestX = box.getCenterX()+box.getWidth()/2;
        }else{
            closestX = circle.getCenterX();
        }
        if (circle.getCenterY() <= box.getCenterY() - box.getHeight()/2){
            closestY = box.getCenterY() - box.getHeight()/2;
        }else if (circle.getCenterY() >= box.getCenterY() + box.getHeight()/2){
            closestY = box.getCenterY() + box.getHeight()/2;
        }else{
            closestY = circle.getCenterY();
        }
        distance = (float) Math.sqrt(Math.pow((circle.getCenterX() - closestX), 2) + Math.pow(circle.getCenterY()-closestY, 2));

        return distance;
    }
}
