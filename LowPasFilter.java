package com.example.owner.projekat;

import java.io.Serializable;

/**
 * Created by Owner on 7/1/2017.
 */

public class LowPasFilter implements Serializable{
    protected float oldValues[];
    protected boolean noOldValues;
    protected float alfa;

    public LowPasFilter(float alfa) {
        this.alfa = alfa;
        oldValues = new float[3];
        noOldValues = true;
    }


    public void filter(float[] values) {
        for (int i = 0; i < values.length; i++) {
            if (noOldValues) {
                oldValues[i] = values[i];
            } else {
                values[i] = values[i] * alfa + (1 - alfa) * oldValues[i];
                oldValues[i] = values[i];
            }
        }
        noOldValues = false;
    }

}
