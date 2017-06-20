package com.example.owner.projekat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.owner.projekat.database.MapScoreContract;
import com.example.owner.projekat.database.ScoreReaderDbHelper;
import com.example.owner.projekat.constants.ConstantValues;

import java.io.File;
import java.io.FilenameFilter;

public class StatsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    protected static final String COLUMNS[]=new String[]{MapScoreContract.ScoreEntry.PLAYER_NAME, MapScoreContract.ScoreEntry.TIME};
    protected static final int DESTINATION_IDS[]=new int[]{ android.R.id.text1, android.R.id.text2};
    public static final String MAP_NAME_STATS="com.example.owner.projekat.MAP_NAME_STATS";
    private ListView lw;
    private Spinner spinner;
    private ScoreReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        String a[]=getMapList();
        String mapn=null;
        if(a.length > 0) mapn=a[0];

        Intent intent=getIntent();
        if(intent!=null){
            Bundle bundle=intent.getExtras();
            if(bundle!=null) mapn=bundle.getString(MAP_NAME_STATS);
        }

        spinner=(Spinner)findViewById(R.id.mapspinne);
        lw=(ListView)findViewById(R.id.scorelist);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, a);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(mapn));
        spinner.setOnItemSelectedListener(this);
       // TextView headet=new TextView(this);
        //headet.setText("Score");
        //lw.addHeaderView(headet);
        /*
        Cursor c=getScoreList(mapn);
        if(c!=null) {
            SimpleCursorAdapter cursorAdapter=new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, c, COLUMNS, DESTINATION_IDS, 0);
            lw.setAdapter(cursorAdapter);
        }
        */
    }

    private String[] getMapList(){
        File dir=new File(Environment.getExternalStorageDirectory()+"/Documents/"+ ConstantValues.DIR_NAME);
        String a[]=dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File f = new File(dir, name);
                return f.isFile();
            }
        });
        String[] b=new String[a.length];
        for (int i=0; i<a.length; i++){
            b[i]=(a[i]).substring(0, (a[i]).lastIndexOf('.'));
        }
        return b;
    }

    public Cursor getScoreList(String mapname){
        Cursor c=null;
        if(mapname==null) return c;
        if(dbHelper==null) dbHelper=new ScoreReaderDbHelper(getApplicationContext());
        SQLiteDatabase db=dbHelper.getReadableDatabase();

        String[] projection={
                MapScoreContract.ScoreEntry._ID,
                MapScoreContract.ScoreEntry.PLAYER_NAME,
                MapScoreContract.ScoreEntry.TIME
        };
        String selection= MapScoreContract.ScoreEntry.MAP_NAME+"=?";
        String[] selectionArgs={ mapname};
        String sortOrder= MapScoreContract.ScoreEntry.TIME+" ASC";
        c=db.query(false, MapScoreContract.ScoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null);
        return c;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.clearpol:
                clearPolygonData();
                break;
            case R.id.clearall:
                clearAllData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearPolygonData(){
        String name=spinner.getSelectedItem().toString();
        String selection= MapScoreContract.ScoreEntry.MAP_NAME+ "=?";
        String[] selectionArgs={ name};
        if(dbHelper==null) dbHelper=new ScoreReaderDbHelper(getApplicationContext());
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(MapScoreContract.ScoreEntry.TABLE_NAME, selection, selectionArgs);
        lw.setAdapter(null);
    }

    private void clearAllData(){
        if(dbHelper==null) dbHelper=new ScoreReaderDbHelper(getApplicationContext());
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(MapScoreContract.ScoreEntry.TABLE_NAME, null, null);
        lw.setAdapter(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mapname=((TextView)view).getText().toString();
        Cursor c=getScoreList(mapname);
        SimpleCursorAdapter cursorAdapter=new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, c, COLUMNS, DESTINATION_IDS, 0);
        lw.setAdapter(cursorAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
