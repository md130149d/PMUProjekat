package com.example.owner.projekat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;


public class StartPoint implements Serializable {
    private float x, y, r;
    private float xs, ys;
    private double vx, vy;
    private double dt;
    private int c, width, height;
    private Model model;

    public StartPoint() {
        c = Color.BLUE;
    }

    public StartPoint(float x, float y, float r) {
        c = Color.BLUE;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
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
        this.r = r;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public synchronized void moveGame(float ax, float ay, double dt) {
        float x1, y1;
        this.dt = dt;
        float orgAx = ax;
        float orgAy = ay;
        int orgSgnVx = (vx >= 0) ? 1 : -1;
        int orgSgnVy = (vy >= 0) ? 1 : -1;

        if (!(vx == 0 && vy == 0)) {
            int sgnx = (vx > 0) ? -1 : 1;
            int sgny = (vy > 0) ? -1 : 1;

            double qx = Math.acos(Math.abs(vx) / Math.sqrt(vx * vx + vy * vy));

            if (vx != 0)
                ax = (float) (ax + sgnx * model.MASA * model.getFtr() * Math.cos(qx));
            if (vy != 0)
                ay = (float) (ay + sgny * model.MASA * model.getFtr() * Math.sin(qx));
        }


        xs = (float) (vx * dt + ax * dt * dt / 2);
        ys = (float) (vy * dt + ay * dt * dt / 2);

        vx = vx + ax * dt;
        vy = vy + ay * dt;

        int sgnVx = (vx >= 0) ? 1 : -1;
        int sgnVy = (vy >= 0) ? 1 : -1;

        x += xs;
        y += ys;

        if (orgAx == 0 && orgSgnVx != sgnVx) vx = 0;
        if (orgAy == 0 && orgSgnVy != sgnVy) vy = 0;

        if ((x - r) <= 0 || x + r >= width) {
            vx *= -1;
            if ((x - r) <= 0) x = r;
            else x = width - r;
        }
        if (y - r <= 0 || y + r >= height) {
            vy *= -1;
            if (y - r <= 0) y = r;
            else y = height - r;
        }
    }

    public synchronized float getXPath() {
        return xs;
    }

    public synchronized float getYPath() {
        return ys;
    }

    public synchronized double getDt() {
        return dt;
    }

    public boolean isInSpace(float x, float y) {
        if (Math.abs(this.x - x) <= r && Math.abs(this.y - y) < r) {
            return true;
        }
        return false;
    }

    public synchronized void draw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(c);
        canvas.drawCircle(x, y, r, p);
        p.setColor(Color.rgb(75, 0, 130));
        canvas.drawCircle(x, y, r / 2, p);
    }

    public void setColor(int color) {
        c = color;
    }

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

    public void setBoxDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void interact(Obstacle o) {
        float x1 = o.getX1(), x2 = o.getX2(), y1 = o.getY1(), y2 = o.getY2();

        if (y1 <= y + r && y + r <= y2 && x - r >= x1 && x + r <= x2) {
            vy *= -1;
            if (y + r >= y1) y = y1 - r;
            return;
        }
        if (y2 >= y - r && y1 <= y - r && x - r >= x1 && x + r <= x2) {
            vy *= -1;
            if (y - r <= y2) y = y2 + r;
            return;
        }
        if (x1 <= x + r && x2 >= x + r && y1 <= y && y2 >= y) {
            vx *= -1;
            if (x + r >= x1) x = x1 - r;
            return;
        }
        if (x - r <= x2 && x1 <= x - r && y1 <= y && y2 >= y) {
            vx *= -1;
            if (x - r <= x2) x = x2 + r;
            return;
        }
    }

    public boolean intersectBounds(float xp1, float yp1, float xp2, float yp2) {
        float x1 = x - r;
        float y1 = y - r;
        float x2 = x + r;
        float y2 = y + r;
        if (x2 < xp1 || x1 > xp2 || y1 > yp2 || y2 < yp1) return false;
        return true;
    }

}
