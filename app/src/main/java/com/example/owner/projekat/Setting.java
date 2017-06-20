package com.example.owner.projekat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.owner.projekat.constants.ConstantValues;

public class Setting extends AppCompatActivity {

    private Model model;
    private SeekBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            model = (Model) b.getSerializable(Model.MODEL_KEY);
        }

        EditText text = (EditText) findViewById(R.id.ftr);
        text.setText(Float.toString(model.getFtr()));

        text = (EditText) findViewById(R.id.factor);
        text.setText(Float.toString(model.getFactor()));

        bar = (SeekBar) findViewById(R.id.sound);
        bar.setMax(ConstantValues.MAX_SOUND_VALUE);
        bar.setProgress(model.getSound());
    }

    public void save(View view) {
        boolean indtr = false, indfactor = false, indradius;
        float tr = 0, factor = 0;
        String spom;
        TextView textView = (TextView) findViewById(R.id.ftr);
        spom = textView.getText().toString();
        if (!spom.isEmpty()) {
            indtr = true;
            tr = Float.parseFloat(spom);
        }
        textView = (TextView) findViewById(R.id.factor);
        spom = textView.getText().toString();
        if (!spom.isEmpty()) {
            indfactor = true;
            factor = Float.parseFloat(spom);
        }

        indradius = true;

        if (indfactor || indradius || indtr) {
            SharedPreferences sp = getSharedPreferences(ConstantValues.PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if (indtr) editor.putFloat(ConstantValues.FTR_KEY, tr);
            if (indfactor) editor.putFloat(ConstantValues.FACTOR_KEY, factor);
            if (indradius) editor.putInt(ConstantValues.SOUND_KEY, bar.getProgress());
            //model.setRadius(radius);
            model.setSound(bar.getProgress());
            model.setFtr(tr);
            model.setFactor(factor);
            editor.commit();
        }
        //finish();
    }

    public void setDefaultValues(View view) {
        SharedPreferences sp = getSharedPreferences(ConstantValues.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(ConstantValues.FTR_KEY, ConstantValues.DEFAULT_FTR_VALUE);
        editor.putFloat(ConstantValues.FACTOR_KEY, ConstantValues.DEFAULT_FACTOR_VALUE);
        //editor.putFloat(ConstantValues.RADIUS_KEY, ConstantValues.DEFAULT_RADIUS_VALUE);
        editor.putInt(ConstantValues.SOUND_KEY, ConstantValues.SOUND_DEFAULT);
        model.setSound(ConstantValues.SOUND_DEFAULT);
        model.setFtr(ConstantValues.DEFAULT_FTR_VALUE);
        model.setFactor(ConstantValues.DEFAULT_FACTOR_VALUE);
        editor.commit();
        TextView textView = (TextView) findViewById(R.id.ftr);
        textView.setText("" + ConstantValues.DEFAULT_FTR_VALUE);
        textView = (TextView) findViewById(R.id.factor);
        textView.setText("" + ConstantValues.DEFAULT_FACTOR_VALUE);
        bar.setProgress(ConstantValues.SOUND_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Model.MODEL_KEY, model);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }
}
