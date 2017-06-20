package com.example.owner.projekat.database;

import android.provider.BaseColumns;

/**
 * Created by Owner on 4/6/2017.
 */

public final class MapScoreContract {

    private MapScoreContract(){}

    public static class ScoreEntry implements BaseColumns{
        public static final String TABLE_NAME="score";
        public static final String MAP_NAME="map";
        public static final String PLAYER_NAME="player";
        public static final String TIME="time";
    }
}
