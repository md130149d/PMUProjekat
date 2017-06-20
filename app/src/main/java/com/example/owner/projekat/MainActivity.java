package com.example.owner.projekat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.owner.projekat.constants.ConstantValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {  //, SimpleAdapter.ViewBinder{

    private Model model;
    private ListView lv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new Model();
        float ftr, factor;// radius,
        int sound;
        SharedPreferences sp = getSharedPreferences(ConstantValues.PREFERENCE_FILE, Context.MODE_PRIVATE);
        if (!sp.contains(ConstantValues.FTR_KEY)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(ConstantValues.FTR_KEY, ConstantValues.DEFAULT_FTR_VALUE);
            editor.putFloat(ConstantValues.FACTOR_KEY, ConstantValues.DEFAULT_FACTOR_VALUE);
            //editor.putFloat(ConstantValues.RADIUS_KEY, ConstantValues.DEFAULT_RADIUS_VALUE);
            editor.putInt(ConstantValues.SOUND_KEY, ConstantValues.SOUND_DEFAULT);
            editor.apply();
            ftr = ConstantValues.DEFAULT_FTR_VALUE;
            //radius = ConstantValues.DEFAULT_RADIUS_VALUE;
            sound=ConstantValues.SOUND_DEFAULT;
            factor = ConstantValues.DEFAULT_FACTOR_VALUE;
        } else {
            ftr = sp.getFloat(ConstantValues.FTR_KEY, ConstantValues.DEFAULT_FTR_VALUE);
            //radius = sp.getFloat(ConstantValues.RADIUS_KEY, ConstantValues.DEFAULT_RADIUS_VALUE);
            sound=sp.getInt(ConstantValues.SOUND_KEY, ConstantValues.SOUND_DEFAULT);
            factor = sp.getFloat(ConstantValues.FACTOR_KEY, ConstantValues.DEFAULT_FACTOR_VALUE);
        }

        lv = (ListView) findViewById(R.id.map_list);
        String a[] = returnFileList();
        adapter=new MyAdapter();
        adapter.setFileName(a);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, a);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        //lv.setOnItemLongClickListener(this);
        registerForContextMenu(lv);
        model.setFactor(factor);
        model.setFtr(ftr);
        //model.setRadius(radius);
        model.setSound(sound);
    }

    protected String[] returnFileList() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Documents/" + ConstantValues.DIR_NAME);
        String[] a = f.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File nf = new File(dir, name);
                return nf.isFile();
            }
        });
        String[] b=new String[a.length];
        for (int i=0; i<a.length; i++){
            b[i]=(a[i]).substring(0, (a[i]).lastIndexOf('.'));
        }
        return b;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.floating_menu, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lv!=null){
            String a[] = returnFileList();
            adapter=new MyAdapter();
            adapter.setFileName(a);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(this);
        }
        /*
        model=new Model();
        float ftr, radius, factor;
        SharedPreferences sp = getSharedPreferences(ConstantValues.PREFERENCE_FILE, Context.MODE_PRIVATE);
        if (!sp.contains(ConstantValues.FTR_KEY)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(ConstantValues.FTR_KEY, ConstantValues.DEFAULT_FTR_VALUE);
            editor.putFloat(ConstantValues.FACTOR_KEY, ConstantValues.DEFAULT_FACTOR_VALUE);
            editor.putFloat(ConstantValues.RADIUS_KEY, ConstantValues.DEFAULT_RADIUS_VALUE);
            editor.apply();
            ftr = ConstantValues.DEFAULT_FTR_VALUE;
            radius = ConstantValues.DEFAULT_RADIUS_VALUE;
            factor = ConstantValues.DEFAULT_FACTOR_VALUE;
        } else {
            ftr = sp.getFloat(ConstantValues.FTR_KEY, ConstantValues.DEFAULT_FTR_VALUE);
            radius = sp.getFloat(ConstantValues.RADIUS_KEY, ConstantValues.DEFAULT_RADIUS_VALUE);
            factor = sp.getFloat(ConstantValues.FACTOR_KEY, ConstantValues.DEFAULT_FACTOR_VALUE);
        }

        model.setFactor(factor);
        model.setFtr(ftr);
        model.setRadius(radius);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newmap:
                newMapCall();
                break;
            case R.id.stats:
                seeStats();
                break;
            case R.id.setings:
                changeSettings();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newMapCall() {
        Intent i = new Intent(getApplicationContext(), Map.class);
        i.putExtra(Model.MODEL_KEY, model);
        startActivity(i);
    }

    protected void changeSettings() {
        Intent i = new Intent(getApplicationContext(), Setting.class);
        i.putExtra(Model.MODEL_KEY, model);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle b=data.getExtras();
        model= (Model) b.getSerializable(Model.MODEL_KEY);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), Game.class);
        TextView v= (TextView) view.findViewById(R.id.mapn);
        String name=v.getText().toString();
        i.putExtra(Model.MAP_NAME, name);
        i.putExtra(Model.MODEL_KEY, model);
        startActivity(i);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.deleteobj:
                View v=info.targetView;
                if(v!=null){
                    TextView view= (TextView) v.findViewById(R.id.mapn);
                    String name=view.getText().toString();
                    deleteMap(name);
                }
                return true;
            default:return super.onContextItemSelected(item);
        }
    }

    private void seeStats(){
        Intent i=new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(i);
    }

    protected void deleteMap(String name){
        File dird=Model.isRootPresent();
        File df=new File(dird, name+".txt");
        if(df.exists()){
            df.delete();
        }
        dird=Model.isPicturePresent();
        File pf=new File(dird, name+".png");
        if(pf.exists()){
            pf.delete();
        }

        String a[] = returnFileList();
        adapter=new MyAdapter();
        adapter.setFileName(a);
        lv.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {

        private String[] fileName;

        void setFileName(String fileName[]){
            this.fileName=fileName;
        }

        @Override
        public int getCount() {
            return fileName.length;
        }

        @Override
        public Object getItem(int position) {
            return fileName[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.map_display, null);
            File dirp=Model.isPicturePresent();
            String mapname=fileName[position]+".png";//(fileName[position]).substring(0, fileName[position].indexOf('.'))+".png";
            File nf=new File(dirp, mapname);
            try {
                TextView textView = (TextView) view.findViewById(R.id.mapn);
                textView.setText(fileName[position]);
                if(nf.exists()) {
                    FileInputStream fis = new FileInputStream(nf);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    ImageView imageView = (ImageView) view.findViewById(R.id.mapimg);
                    imageView.setImageBitmap(bitmap);
                    fis.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
