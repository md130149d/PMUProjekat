package com.example.owner.projekat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.owner.projekat.database.MapScoreContract;
import com.example.owner.projekat.database.ScoreReaderDbHelper;
import com.example.owner.projekat.soundservice.SoundService;

public class Game extends AppCompatActivity implements SurfaceHolder.Callback, BluePrintMap.FinishedGame,
        NameDialog.PlayerNameCallback, Refresher.OnTimeClick, SensorEventListener {

    private static final String GAME_TIME_ = "com.example.owner.projekat.GAME_TIME";

    private SurfaceView surface;
    private Refresher refresher;
    private String map_name;
    private boolean resumeCallback, surfaceCallback;
    private SensorManager manager;
    private BluePrintMap map;
    private boolean finished;
    private long game_time = 0;
    private int count = 0;
    private long start_time;
    private TextView textTimer;
    private Model model;
    private long oldTime;
    private float[] gravity;
    private LowPasFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        map_name = bundle.getString(Model.MAP_NAME);
        model = (Model) bundle.getSerializable(Model.MODEL_KEY);

        if (savedInstanceState != null) {
            map = (BluePrintMap) savedInstanceState.getSerializable(BluePrintMap.BLUEPRINTMAP);
            map.setContext(getApplicationContext());
            model = (Model) savedInstanceState.getSerializable(Model.MODEL_KEY);
            map.setModel(model);
            game_time = savedInstanceState.getLong(GAME_TIME_);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textTimer = (TextView) findViewById(R.id.textTimer);
        textTimer.setText("" + game_time);

        surface = (SurfaceView) findViewById(R.id.surfacemap);
        surface.setZOrderOnTop(true);

        SurfaceHolder holder = surface.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
    }

    protected void init() {
        if (resumeCallback && surfaceCallback && !finished) {

            gravity = new float[2];
            gravity[0] = (float) 9.81;
            gravity[1] = (float) 9.81;

            if (map == null) {
                map = BluePrintMap.loadMap(map_name, surface.getWidth(), surface.getHeight());
            }
            map.setEnd(this);
            map.setContext(getApplicationContext());
            map.setModel(model);
            start_time = System.currentTimeMillis();

            ImageView image = (ImageView) findViewById(R.id.map_layout);
            Bitmap bitmap = Bitmap.createBitmap(surface.getWidth(), surface.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            map.drawGame(canvas);
            image.setImageBitmap(bitmap);

            Intent intent = new Intent(getApplicationContext(), SoundService.class);
            startService(intent);

            Sensor s;

            refresher = new Refresher(surface.getHolder(), this);
            refresher.setMap(map);
            manager = (SensorManager) getSystemService(SENSOR_SERVICE);
            s = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
            refresher.start();

            resumeCallback = false;
            surfaceCallback = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCallback = true;
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (refresher != null) {
            refresher.interrupt();
            refresher.finishGame();
            //stopService(new Intent(getApplicationContext(), SoundService.class));
        }
        if (manager != null) {
            manager.unregisterListener(this);
            map.removeFinishedGameListener();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceCallback = true;
        init();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void stop(int rez) {
        finished = true;
        if (manager != null) manager.unregisterListener(this);
        if (refresher != null) {
            refresher.interrupt();
            refresher.finishGame();
        }
        if (rez == 2) {
            NameDialog dialog = NameDialog.getInstance(this, map_name);
            dialog.show(getFragmentManager(), "score");
        } else exitGame();
    }

    private void exitGame() {
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
        intent.putExtra(StatsActivity.MAP_NAME_STATS, map_name);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), SoundService.class));
        super.onDestroy();
    }

    @Override
    public void saveScore(String name) {
        if (name == null || name.isEmpty()) {
            exitGame();
            return;
        }
        ScoreReaderDbHelper dbHelper = new ScoreReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MapScoreContract.ScoreEntry.MAP_NAME, map_name);
        values.put(MapScoreContract.ScoreEntry.PLAYER_NAME, name);
        values.put(MapScoreContract.ScoreEntry.TIME, game_time);
        db.insert(MapScoreContract.ScoreEntry.TABLE_NAME, null, values);
        exitGame();
    }

    @Override
    public void increaseTime() {
        long tr_time = System.currentTimeMillis();
        if ((tr_time - start_time) / (double) 1000 >= 1) {
            start_time = tr_time;
            game_time++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String a = "" + Long.toString(game_time);
                    textTimer.setText(a);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.removeFinishedGameListener();
        outState.putSerializable(BluePrintMap.BLUEPRINTMAP, map);
        outState.putSerializable(Model.MODEL_KEY, model);
        outState.putLong(GAME_TIME_, game_time);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (filter == null) filter = new LowPasFilter(Model.ALPHA);
        filter.filter(event.values);

        float ax = event.values[0];
        float ay = event.values[1];
        //System.out.println("AX: "+ax);
        //System.out.println("AY: "+ay);
        if (model.isInHole()) {

            TrapHole trap = model.getTrap();

            float sx = map.getStart().getX();
            float sy = map.getStart().getY();
            float tx = trap.getX();
            float ty = trap.getY();
            float xl = tx - sx;
            float yl = ty - sy;
            float re = (float) Math.sqrt(xl * xl + yl * yl);

            float raz = trap.getR() - re;
            raz = 2;
            double ix=xl/re;
            double iy=yl/re;

            map.getStart().setVx(map.getStart().getVx()+ix);
            map.getStart().setVy(map.getStart().getVy()+iy);
            /*
            double alpha = Math.acos(Math.abs(yl) / Math.sqrt(xl * xl + yl * yl));

            int sgnx;// = (map.getStart().getVx() > 0) ? -1 : 1;
            int sgny;// = (map.getStart().getVy() > 0) ? -1 : 1;

            if (yl > 0) sgny = 1;
            else sgny = -1;

            if (xl > 0) sgnx = 1;
            else sgnx = -1;

            System.out.println("SgnX: " + sgnx);
            System.out.println("SgnY: " + sgny);

            float gax = (float) (sgnx * Math.sin(alpha) * raz);
            float gay = (float) (sgny * Math.cos(alpha) * raz);

            System.out.println("GAX: " + gax);
            System.out.println("GAY: " + gay);
            ax += gax;
            ay += gay;
            */
            /*
            double d = vx * yl - vy * xl;
            double alfa = Math.acos((vx * xl + vy * yl) / (Math.sqrt((vx * vx + vy * vy) * (xl * xl + yl * yl))));
            double beta = Math.PI / 2 - alfa;
            if (d > 0) {
                alfa = -alfa;
            } else if (d < 0) {
                beta = -beta;
            }
            // vlx i vly * factor
            double vlx = (ax * Math.cos(beta) + ay * Math.sin(beta)) * Math.abs(Math.sin(alfa));
            double vly = (ay * Math.cos(beta) - ax * Math.sin(beta)) * Math.abs(Math.sin(alfa));
            double vhx = (ax * Math.cos(alfa) + ay * Math.sin(alfa)) * Math.cos(alfa);
            double vhy = (ay * Math.cos(alfa) - ax * Math.sin(alfa)) * Math.cos(alfa);

            float re = (float) Math.sqrt(xl * xl + yl * yl);
            float raz = trap.getR() - re;
            */
        }


        map.moveBall(ax, ay, event.timestamp / (double) 100000000);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
