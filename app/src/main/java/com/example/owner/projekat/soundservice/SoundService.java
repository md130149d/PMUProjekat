package com.example.owner.projekat.soundservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.owner.projekat.Model;
import com.example.owner.projekat.R;
import com.example.owner.projekat.constants.ConstantValues;

import java.util.ArrayList;
import java.util.List;


public class SoundService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    public static final String ACTION_PLAY="com.example.owner.projekat.soundservice.ACTION_PLAY";
    public static final String ACTION_STOP="com.example.owner.projekat.soundservice.ACTION_STOP";

    private List<MediaPlayer> mediaPlayerList;
    private MediaPlayer player;
    private int i;

    public SoundService(){
        mediaPlayerList=new ArrayList<>();
        i=0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id= R.raw.sound;

        Bundle bundle=intent.getExtras();
        int sound;
        float log1=0;
        if (bundle!=null) {
            Model model = (Model) bundle.getSerializable(Model.MODEL_KEY);
            sound=model.getSound();
            log1=(float)(Math.log(ConstantValues.MAX_SOUND_VALUE-sound)/Math.log(ConstantValues.MAX_SOUND_VALUE));
        }
        //System.out.println("I: "+(i++));
        if(intent.getAction()==null) return START_NOT_STICKY;
        switch (intent.getAction()){
            case ACTION_PLAY:
                player=MediaPlayer.create(getApplicationContext(), id);
                player.setVolume(1-log1, 1-log1);
                player.setOnCompletionListener(this);
                player.setOnPreparedListener(this);
                //player.prepareAsync();
                //player.start();
                break;
            case ACTION_STOP:
                player=MediaPlayer.create(getApplicationContext(), id);
                player.setVolume(1-log1, 1-log1);
                player.setOnCompletionListener(this);
                player.setOnPreparedListener(this);
                break;
            default:break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
/*
    private class SoundSound implements Runnable{

        Model model;
        Context context;

        public SoundSound(Model model, Context context, float lof){
            this.model=model;
            this.context=context;
        }

        @Override
        public void run() {

        }
    }
    */
    public interface endSound{
        void finishedSound();
    }
}
