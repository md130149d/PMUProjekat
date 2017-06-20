package com.example.owner.projekat;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.example.owner.projekat.constants.ConstantValues;

import java.io.File;
import java.io.Serializable;

public class Model implements Serializable {
    public static final String MODEL_KEY = "com.example.owner.projekat.MODEL_KEY";
    public static final String MAP_NAME = "com.example.owner.projekat.MAP_NAME";
    //public static final int MAX_SOUND_STEPS=50;
    public static final int MASA=2;
    private float ftr;
    private float factor;
    private int sound;

    public static File isRootPresent() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Documents/" + ConstantValues.DIR_NAME);
        if (!f.exists()) {
            f.mkdir();
        }
        return f;
    }

    public static File isPicturePresent() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + ConstantValues.DIR_NAME);
        if (!f.exists()) {
            f.mkdir();
        }
        return f;
    }

    public float getFtr() {
        return ftr;
    }

    public void setFtr(float ftr) {
        this.ftr = ftr;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setSound(int sound){
        this.sound=sound;
    }

    public int getSound() {
        return sound;
    }
}
