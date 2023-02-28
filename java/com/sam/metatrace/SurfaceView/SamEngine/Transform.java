package com.sam.metatrace.SurfaceView.SamEngine;

public class Transform {
    private float x;
    private float y;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Transform() {
        this.x = 0f;
        this.y = 0f;
    }

    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(float x, float y){
        this.x += x;
        this.y += y;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }
}

