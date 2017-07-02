package com.example.owner.projekat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Owner on 2/18/2017.
 */

public class TrapHole implements MapObjectInterface {

    private float x, y, r;
    private float r1;
    private int c;

    public TrapHole() {
        c = Color.BLACK;
    }

    public TrapHole(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r * 6 / 4;
        r1 = r;
        c = Color.BLACK;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public float getY() {

        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r * 6 / 4;
        r1 = r;
    }

    @Override
    public boolean isInSpace(float x, float y) {
        if (Math.abs(this.x - x) <= r && Math.abs(this.y - y) < r) {
            return true;
        }
        return false;
    }

    @Override
    public boolean intersectBounds(float xp1, float yp1, float xp2, float yp2) {
        float x1 = x - r;
        float y1 = y - r;
        float x2 = x + r;
        float y2 = y + r;
        if (x2 < xp1 || x1 > xp2 || y1 > yp2 || y2 < yp1) return false;
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.DKGRAY);
        canvas.drawCircle(x, y, r, p);
        p.setColor(Color.BLACK);
        canvas.drawCircle(x, y, 2 * r / 3, p);
    }

    @Override
    public void setColor(int color) {
        c = color;
    }

    @Override
    public String returnFormatInString(int width, int height) {
        StringBuilder builder = new StringBuilder();
        builder.append(1);
        builder.append(":");
        float nx = x / (float) width;
        float ny = y / (float) height;
        builder.append(nx);
        builder.append(":");
        builder.append(ny);
        return builder.toString();
    }

    @Override
    public int interact(StartPoint start) {
        float rz = Math.abs(start.getR() - r1);
        float xl = Math.abs(this.x - start.getX());
        float yl = Math.abs(this.y - start.getY());
        float re = (float) Math.sqrt(xl * xl + yl * yl);
        if (re < rz) {
            return -1;
        }

        if (xl < r && yl < r) {
            return -2;
        }

        return 0;
    }

}
