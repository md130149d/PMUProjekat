package com.example.owner.projekat;

import android.graphics.Canvas;

import java.io.Serializable;


public interface MapObjectInterface extends Serializable{
    boolean isInSpace(float x, float y); // for delete object
    boolean intersectBounds(float x1, float y1, float x2, float y2); // for method can draw
    void draw(Canvas canvas); // draw
    void setColor(int color); // setColor, no use for now
    String returnFormatInString(int width, int height); // string for storing
    int interact(StartPoint start); // does it intersects with the end point
}
