package com.sam.metatrace.SurfaceView.SamEngine.Collisions;

import com.sam.metatrace.SurfaceView.Game1.GameObject;

public interface CollisionListener {
    void onCollisionEnter(GameObject collisionObject);
    void onCollisionStay(GameObject collisionObject);
    void onCollisionExit(GameObject collisionObject);
}
