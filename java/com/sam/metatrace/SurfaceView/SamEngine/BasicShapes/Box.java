package com.sam.metatrace.SurfaceView.SamEngine.BasicShapes;

public class Box {
    private float width;
    private float height;


    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Box(int width, int height){
        this.width = width;
        this.height = height;
    }

    public Box() {
        width = height = 0;
    }
}
