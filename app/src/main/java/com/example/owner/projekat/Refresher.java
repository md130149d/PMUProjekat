package com.example.owner.projekat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceHolder;


public class Refresher extends Thread {//implements SensorEventListener{

    private BluePrintMap map;
    private SurfaceHolder surfaceHolder;
    private OnTimeClick maintime;
    private boolean isfinished;

    public Refresher(SurfaceHolder surfaceHolder, OnTimeClick main){
        this.surfaceHolder=surfaceHolder;
        maintime=main;
    }

    public void setMap(BluePrintMap map){
        this.map=map;
    }

    @Override
    public void run() {
        while(!interrupted() && !isfinished){
            Canvas canvas=surfaceHolder.lockCanvas();
            if(canvas!=null){
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                map.drawStartPoint(canvas);

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {
                sleep(1);
                maintime.increaseTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finishGame(){
        isfinished=true;
    }

    //@Override
    public void onSensorChanged(SensorEvent event) {
        map.moveBall(event.values[0], event.values[1], event.timestamp/(double)100000000);
    }

//    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnTimeClick{
        void increaseTime();
    }

}
