package com.example.owner.projekat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.view.Display;

import com.example.owner.projekat.soundservice.SoundService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BluePrintMap implements Serializable {

    public static final String BLUEPRINTMAP = "com.example.owner.projekat.BLUEPRINT";

    private List<MapObjectInterface> l;

    private List<MapObjectInterface> interactingObjects;
    private List<Boolean> isActive;
    private List<Integer> num;

    private float ox, oy;
    private MapObjectInterface tr, endp;
    private StartPoint start;
    private double timestamp;
    private int width, height;
    private FinishedGame end;
    private Context context;
    private Model model;

    public BluePrintMap() {
        l = new ArrayList<>();
        interactingObjects = new ArrayList<>();
        isActive = new ArrayList<>();
        num = new ArrayList<>();
    }

    public BluePrintMap(String format, float rp, int width, int height) {
        this.width = width;
        this.height = height;

        l = new ArrayList<>();
        interactingObjects = new ArrayList<>();
        isActive = new ArrayList<>();
        num = new ArrayList<>();

        String a[] = format.split(":");
        int indeks = 1, br;
        float x1, x2, y1, y2, r;
        x1 = Float.parseFloat(a[indeks++]) * width;
        y1 = Float.parseFloat(a[indeks++]) * height;
        r = height * rp;
        start = new StartPoint(x1, y1, r);
        br = Integer.parseInt(a[indeks++]);
        for (int i = 0; i < br; i++) {
            int tip = Integer.parseInt(a[indeks++]);
            MapObjectInterface moi;
            if (tip == 2) {
                x1 = Float.parseFloat(a[indeks++]) * width;
                y1 = Float.parseFloat(a[indeks++]) * height;
                x2 = Float.parseFloat(a[indeks++]) * width;
                y2 = Float.parseFloat(a[indeks++]) * height;
                moi = new Obstacle(x1, y1, x2, y2);
            } else {
                x1 = Float.parseFloat(a[indeks++]) * width;
                y1 = Float.parseFloat(a[indeks++]) * height;
                r = height * rp;
                moi = new TrapHole(x1, y1, r);
            }
            l.add(moi);
        }
        indeks++;
        x1 = Float.parseFloat(a[indeks++]) * width;
        y1 = Float.parseFloat(a[indeks++]) * height;
        r = height * rp;
        endp = new EndPoint(x1, y1, r);
    }

    @Nullable
    public static BluePrintMap loadMap(String name, int width, int height) {
        File dir = Model.isRootPresent();
        File f = new File(dir, name + ".txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            String format = bufferedReader.readLine();
            BluePrintMap map = new BluePrintMap(format, (float) 0.05, width, height);
            return map;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setEnd(FinishedGame end) {
        this.end = end;
    }

    public MapObjectInterface getTr() {
        return tr;
    }

    public void setTr(MapObjectInterface tr) {
        this.tr = tr;
    }

    public StartPoint getStart() {
        return start;
    }

    public void setStart(StartPoint start) {
        this.start = start;
    }

    public MapObjectInterface getEndp() {
        return endp;
    }

    public void setCoordinates(float x, float y) {
        ox = x;
        oy = y;
    }

    public void addObject(MapObjectInterface moi, int ind) {
        if (ind == 2) endp = moi;
        else l.add(moi);
        tr = null;
    }

    public void removeObject(float x, float y) {
        MapObjectInterface moi = null;
        if (start != null)
            if (start.isInSpace(x, y)) {
                start = null;
                return;
            }
        if (endp != null)
            if (endp.isInSpace(x, y)) {
                endp = null;
                return;
            }
        for (MapObjectInterface mo : l) {
            if (mo.isInSpace(x, y)) {
                moi = mo;
                break;
            }
        }
        if (moi != null) {
            l.remove(moi);
        }
    }

    public void draw(Canvas canvas) {

        for (MapObjectInterface moi : l) {
            moi.draw(canvas);
        }
        if (tr != null) {
            tr.draw(canvas);
        }
        if (endp != null) endp.draw(canvas);
        if (start != null) start.draw(canvas);
    }

    public void drawGame(Canvas canvas) {
        for (MapObjectInterface moi : l) {
            moi.draw(canvas);
        }
        endp.draw(canvas);
    }

    public synchronized void drawStartPoint(Canvas canvas) {
        start.draw(canvas);
    }

    public String getMapString(int width, int height) {
        StringBuilder builder = new StringBuilder();

        builder.append(start.returnFormatInString(width, height));
        builder.append(":");
        int br = l.size();
        builder.append(br);
        builder.append(":");
        for (MapObjectInterface moi : l) {
            builder.append(moi.returnFormatInString(width, height));
            builder.append(":");
        }
        builder.append(endp.returnFormatInString(width, height));
        return builder.toString();
    }

    public void setModel(Model model) {
        this.model = model;
        start.setModel(model);
    }

    public synchronized void moveBall(float ax, float ay, double time) {
        if (timestamp != 0) {

            Collections.fill(isActive, Boolean.FALSE);

            double dt = time - timestamp;
            start.moveGame(ay, ax, dt);
            for (MapObjectInterface moi : l) {
                int pos = moi.interact(start);
                if (pos == -1) {
                    Intent intent = new Intent(context, SoundService.class);
                    intent.setAction(SoundService.ACTION_STOP);
                    intent.putExtra(Model.MODEL_KEY, model);
                    context.startService(intent);
                    end.stop(pos);
                }
                else if (pos == 1) {

                    boolean hit = true;
                    int position = 0;
                    for (Iterator<MapObjectInterface> it = interactingObjects.iterator(); it.hasNext(); ) {

                        MapObjectInterface ob = it.next();
                        if (ob == moi) {
                            hit = false;
                            isActive.set(position, true);
                            num.set(position, 5);
                            break;
                        }
                        position++;
                    }

                    if (hit == true) {
                        try {
                            Intent intent = new Intent(context, SoundService.class);
                            intent.setAction(SoundService.ACTION_PLAY);
                            intent.putExtra(Model.MODEL_KEY, model);
                            context.startService(intent);
                            interactingObjects.add(moi);
                            isActive.add(true);
                            num.add(5);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.sound);
                    //mediaPlayer.start();
                }
            }

            int position = 0;
            for (Iterator<Boolean> it = isActive.iterator(); it.hasNext(); ) {
                Boolean b = it.next();
                int no = num.get(position);
                no--;
                num.set(position, no);
                if (!b && no == 0) {
                    it.remove();
                    num.set(position, -2);
                }
                position++;
            }

            position=0;
            for (Iterator<MapObjectInterface> it = interactingObjects.iterator(); it.hasNext(); ) {
                int vr=num.get(position);
                MapObjectInterface moi=it.next();
                if(vr<=-2){
                    it.remove();
                }
                position++;
            }

            for (Iterator<Integer> it = num.iterator(); it.hasNext(); ) {
                int vr=it.next();
                if(vr<=-2){
                    it.remove();
                }
            }

            int pos = endp.interact(start);
            if (pos == 2) end.stop(pos);
            timestamp = time;
        } else {
            timestamp = time;
            start.setBoxDimension(width, height);
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void removeFinishedGameListener() {
        end = null;
        context = null;
    }

    public interface FinishedGame {
        void stop(int rez);
    }

    public void clearMap() {
        start = null;
        endp = null;
        for (Iterator<MapObjectInterface> i = l.iterator(); i.hasNext(); ) {
            MapObjectInterface moi = i.next();
            i.remove();
        }
    }

    public boolean canBeDrawn(float x1, float y1, float x2, float y2) {

        for (MapObjectInterface moi : l) {
            if (moi.intersectBounds(x1, y1, x2, y2)) return false;
        }
        if (start != null) {
            if (start.intersectBounds(x1, y1, x2, y2)) return false;
        }
        return true;
    }

}
