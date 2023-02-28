package com.sam.metatrace.SurfaceView.SamEngine.Collisions;

import com.sam.metatrace.SurfaceView.Game1.GameObject;

public interface BaseCollider {
    /**
     * 不可靠的方法，可能会不按照预期多次被调用
     */
    void _onCollisionEnterOrExit(GameObject collideObject);
    void _onCollisionStay(GameObject collideObject);
    void _onNotCollision(GameObject collideObject);
    float getCenterX();
    float getCenterY();
    int getColliderGroupId();
    Class getColliderClass();
}
