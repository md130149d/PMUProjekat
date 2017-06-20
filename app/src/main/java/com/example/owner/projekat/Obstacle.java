package com.example.owner.projekat;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;

/**
 * Created by Owner on 2/17/2017.
 */

public class Obstacle implements MapObjectInterface {

    private float ox, oy;
    private float x1, y1, x2, y2;
    private int c;

    public Obstacle() {
        c = Color.BLACK;
    }

    public Obstacle(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        c = Color.BLACK;
    }

    public void setCoordinates(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    @Override
    public boolean isInSpace(float x, float y) {
        if (x >= x1 && x <= x2 && y >= y1 && y <= y2) return true;
        else return false;
    }

    @Override
    public boolean intersectBounds(float xp1, float yp1, float xp2, float yp2) {
        if (x2 < xp1 || x1 > xp2 || y1 > yp2 || y2 < yp1) return false;
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(x1, y1, x2, y2, paint);
    }

    @Override
    public void setColor(int color) {
        c = color;
    }

    @Override
    public String returnFormatInString(int width, int height) {
        StringBuilder builder = new StringBuilder();
        builder.append(2);
        builder.append(":");
        float nx1 = x1 / (float) width;
        float nx2 = x2 / (float) width;
        float ny1 = y1 / (float) height;
        float ny2 = y2 / (float) height;
        builder.append(nx1);
        builder.append(":");
        builder.append(ny1);
        builder.append(":");
        builder.append(nx2);
        builder.append(":");
        builder.append(ny2);
        return builder.toString();
    }

    @Override
    public int interact(StartPoint start) {
        float x = start.getX(), y = start.getY(), r = start.getR(), xc = -1, yc = -1;
        double vx = start.getVx(), vy = start.getVy();
        double dt = start.getDt();
        float factor = start.getModel().getFactor();

        if (y1 <= y + r && y + r <= y2 && x >= x1 && x <= x2) {
            //vy*=-1;
            start.setVy(-vy * (1 - factor));
            if (y + r >= y1) start.setY(y1 - r);
            return 1;
        }
        if (y2 >= y - r && y1 <= y - r && x >= x1 && x <= x2) {
            //vy*=-1;
            start.setVy(-vy * (1 - factor));
            if (y - r <= y2) start.setY(y2 + r);
            return 1;
        }
        if (x1 <= x + r && x2 >= x + r && y1 <= y && y2 >= y) {
            //vx*=-1;
            start.setVx(-vx * (1 - factor));
            if (x + r >= x1) start.setX(x1 - r);
            return 1;
        }
        if (x - r <= x2 && x1 <= x - r && y1 <= y && y2 >= y) {
            //vx*=-1;
            start.setVx(-vx * (1 - factor));
            if (x - r <= x2) start.setX(x2 + r);
            return 1;
        }

        float xu = x1, yu = y1;
        for (int i = 0; i < 4; i++) {
            if (Math.sqrt((xu - x) * (xu - x) + (yu - y) * (yu - y)) < r) {
                xc = xu;
                yc = yu;
                //               System.out.println("Stara pozicija: "+x+" y: "+y);
                float xs = start.getXPath(), ys = start.getYPath();
                float rp = (float) Math.sqrt((xu - x) * (xu - x) + (yu - y) * (yu - y));
                float re = (float) ((r - rp) / (Math.sqrt(xs * xs + ys * ys)));
                float dx = xs * re;
                float dy = ys * re;
                x-=dx;
                y-=dy;
//                System.out.println("Nova pozicija: "+x+" y: "+y);
                start.setX(x);
                start.setY(y);
                System.out.println("Novo rastojanje: "+(float)Math.sqrt((xu - x) * (xu - x) + (yu - y) * (yu - y))+" "+r);
                break;
            }
//            if (xc != -1 && yc != -1) break;
            if ((i + 1) % 2 == 0) xu = x1;
            else xu = x2;
            if ((i + 1) < 2) yu = y1;
            else yu = y2;
        }


        if (xc != -1 && yc != -1) {
            System.out.println("Stara brzina: " + Math.sqrt(vx * vx + vy * vy));
            System.out.println("vx: " + vx + " vy: " + vy);
            float xl = xc - x, yl = yc - y;
            //System.out.println("Razlika x: "+xl + ", y: "+yl);

            double d = vx * yl - vy * xl;
            double alfa = Math.acos((vx * xl + vy * yl) / (Math.sqrt((vx * vx + vy * vy) * (xl * xl + yl * yl))));
            double beta = Math.PI / 2 - alfa;
            if (d > 0) {
                alfa = -alfa;
            } else if (d < 0) {
                beta = -beta;
            }
            // vlx i vly * factor
            double vlx = (vx * Math.cos(beta) + vy * Math.sin(beta)) * Math.abs(Math.sin(alfa));
            double vly = (vy * Math.cos(beta) - vx * Math.sin(beta)) * Math.abs(Math.sin(alfa));
            double vhx = (vx * Math.cos(alfa) + vy * Math.sin(alfa)) * Math.cos(alfa) * (-1) * (1 - factor);
            double vhy = (vy * Math.cos(alfa) - vx * Math.sin(alfa)) * Math.cos(alfa) * (-1) * (1 - factor);

            /*
            if (Math.abs(xc - x) < start.getR() && Math.abs(yc - y) < start.getR()) {
                start.setX(start.getX());
                start.setY(start.getY());
            }
            */
            vx = vlx + vhx;
            vy = vly + vhy;

            System.out.println("Nova brzina: " + Math.sqrt(vx * vx + vy * vy));
            System.out.println("Nvx: " + vx + " Nvy: " + vy);
            start.setVx(vx);
            start.setVy(vy);
            return 1;
        }
        return 0;
    }

    public void setDrawCoordinates(float x, float y) {
        ox = x;
        oy = y;
    }

    public void updateCoordinates(float nx, float ny) {
        x1 = (ox < nx) ? ox : nx;
        x2 = (ox < nx) ? nx : ox;
        y1 = (oy < ny) ? oy : ny;
        y2 = (oy < ny) ? ny : oy;
    }

    public float getOx() {
        return ox;
    }

    public float getOy() {
        return oy;
    }

}
