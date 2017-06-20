package com.example.owner.projekat.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.owner.projekat.database.MapScoreContract.ScoreEntry;
/**
 * Created by Owner on 4/6/2017.
 */

public class ScoreReaderDbHelper extends SQLiteOpenHelper {

    private static final String DATABSE_NAME="hopebscore.db";

    private static final String CREATE_TABLE="CREATE TABLE "
            + ScoreEntry.TABLE_NAME+"( "+ ScoreEntry._ID
            + " INTEGER PRIMARY KEY, " + ScoreEntry.MAP_NAME+ " TEXT, "
            + ScoreEntry.PLAYER_NAME + " TEXT, "
            + ScoreEntry.TIME+ " BIGINT);";


    public ScoreReaderDbHelper(Context context){
        super(context, DATABSE_NAME, null, 1);
    }

    public ScoreReaderDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ScoreReaderDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
