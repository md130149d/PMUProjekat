package com.example.owner.projekat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Map extends AppCompatActivity implements View.OnTouchListener, BluePrint.Initializer
        , SaveDialog.SaveDialogCallback {

    protected BluePrint blue;
    protected BluePrintMap map;
    protected int ind, height, width;
    protected double pr;
    private Model model;
    private RelativeLayout layout;
    private int showWidth;
    private boolean longClick;
    private float lx, ly, eps;
    private long firstTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        model = (Model) b.getSerializable(Model.MODEL_KEY);
        ind = 0;
        if (savedInstanceState != null) {
            map = (BluePrintMap) savedInstanceState.getSerializable(BluePrintMap.BLUEPRINTMAP);
        } else map = new BluePrintMap();
        blue = (BluePrint) findViewById(R.id.nmap);
        blue.setInitializer(this);
        blue.setOnTouchListener(this);

      //  pr = model.getRadius();
        pr=0.05;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.newmap);
        //actionBar.hide();
        //layout = (RelativeLayout) findViewById(R.id.llayouthiden);
        //layout.setVisibility(View.GONE);
        eps = 30;
        /*
        Spinner spinner = (Spinner) findViewById(R.id.spinermenu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.editor_map, android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ind = 0;
                        break;
                    case 1:
                        ind = 1;
                        break;
                    case 2:
                        ind = 2;
                        break;
                    case 3:
                        ind = 3;
                        break;
                    case 4:
                        ind = 4;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.object_menu, menu);
        return true;
    }

    private void clearMap(){
        map.clearMap();
        blue.invalidate();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.obstical_button);
        if (item != null) {
            View v = item.getActionView();
            Button b = (Button) v.findViewById(R.id.open_menu);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    if (layout.getVisibility() == View.GONE) layout.setVisibility(View.VISIBLE);
                    else layout.setVisibility(View.GONE);
                    */
                    onSaveButtonClick(v);
                }
            });

            Spinner spinner = (Spinner) v.findViewById(R.id.obj_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.editor_map, android.R.layout.simple_spinner_item);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            ind = 0;
                            break;
                        case 1:
                            ind = 1;
                            break;
                        case 2:
                            ind = 2;
                            break;
                        case 3:
                            ind = 3;
                            break;
                        case 4:
                            ind = 4;
                            break;
                        case 5:
                            clearMap();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.obstical_button:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClick(View view) {
        if (map.getStart() != null && map.getEndp() != null) {
            SaveDialog saveDialog = SaveDialog.newInstance(this);
            saveDialog.show(getFragmentManager(), "dialog");
        } else {
            String message;
            if (map.getStart() == null && map.getEndp() == null)
                message = "Start and End positions are both obligatory";
            else if (map.getStart() == null && map.getEndp() != null)
                message = "Game must have a start position.";
            else message = "Game must have an end position.";
            DialogMessage dm = DialogMessage.newInstance(message);
            dm.show(getFragmentManager(), "meh");
        }
    }

    public void onHideButtonClick(View view) {
        //layout.setVisibility(View.GONE);
        //longClick = false;
    }

    protected void executeAction(int action, float x, float y, BluePrint bluePrint) {
        switch (ind) {
            case 0:
                if (map.getStart() == null) {
                    if (action == 2) {
                        StartPoint st = new StartPoint();
                        st.setX(x);
                        st.setY(y);
                        st.setR((float) (pr * height));
                        float x1=st.getX()-st.getR();
                        float y1=st.getY()-st.getR();
                        float x2=st.getX()+st.getR();
                        float y2=st.getY()+st.getR();
                        if(map.canBeDrawn(x1, y1, x2, y2)) map.setStart(st);
                        else Toast.makeText(getApplicationContext(), "Can't plase an object here", Toast.LENGTH_SHORT).show();
                    }
                    bluePrint.invalidate();
                } //else Toast.makeText(getApplicationContext(), "Start position already exists", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (map.getEndp() == null) {
                    if (action == 2) {
                        map.setCoordinates(x, y);
                        EndPoint st = new EndPoint();
                        st.setX(x);
                        st.setY(y);
                        st.setR((float) (pr * height));
                        float x1=st.getX()-st.getR();
                        float y1=st.getY()-st.getR();
                        float x2=st.getX()+st.getR();
                        float y2=st.getY()+st.getR();
                        if(map.canBeDrawn(x1, y1, x2, y2)) map.addObject(st, 2);
                        else Toast.makeText(getApplicationContext(), "Can't plase an object here", Toast.LENGTH_SHORT).show();
                    }
                    bluePrint.invalidate();
                } //else Toast.makeText(getApplicationContext(), "End position already exists", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (action == 0) {
                    Obstacle obs = new Obstacle();
                    obs.setDrawCoordinates(x, y);
                    map.setTr(obs);
                } else if (action == 1) {
                    Obstacle obs = (Obstacle) map.getTr();
                    float ox=obs.getOx();
                    float oy=obs.getOy();
                    float x1=(ox < x) ? ox : x;
                    float y1=(oy < y) ? oy : y;
                    float x2=(ox < x) ? x : ox;
                    float y2=(oy < y) ? y : oy;

                    if(map.canBeDrawn(x1, y1, x2, y2)) obs.updateCoordinates(x, y);
                    bluePrint.invalidate();
                } else {
                    Obstacle obs = (Obstacle) map.getTr();
                    float ox=obs.getOx();
                    float oy=obs.getOy();
                    float x1=(ox < x) ? ox : x;
                    float y1=(oy < y) ? oy : y;
                    float x2=(ox < x) ? x : ox;
                    float y2=(oy < y) ? y : oy;

                    if(map.canBeDrawn(x1, y1, x2, y2)) obs.updateCoordinates(x, y);
                    map.addObject(map.getTr(), 0);
                    bluePrint.invalidate();
                }
                break;
            case 3:
                if (action == 2) {
                    map.setCoordinates(x, y);
                    TrapHole st = new TrapHole();
                    st.setX(x);
                    st.setY(y);
                    st.setR((float) (pr * height));
                    float x1=st.getX()-st.getR();
                    float y1=st.getY()-st.getR();
                    float x2=st.getX()+st.getR();
                    float y2=st.getY()+st.getR();
                    if(map.canBeDrawn(x1, y1, x2, y2)) map.addObject(st, -1);
                    bluePrint.invalidate();
                }
                break;
            case 4:
                if (action == 2) {
                    map.removeObject(x, y);
                    bluePrint.invalidate();
                }
                break;
            /*
            case 5:
                if (action == 0) {
                    map.selectObject(x, y);
                    bluePrint.invalidate();
                } else if (action == 1) {
                    map.moveSelected(x, y);
                    bluePrint.invalidate();
                } else {
                    map.deselectObject();
                    bluePrint.invalidate();
                }
                break;
                */
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x, y;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lx = event.getX();
                ly = event.getY();
                /*
                if (lx >= showWidth) {
                    firstTouch = System.currentTimeMillis();
                    longClick = true;
                }
                */
                executeAction(0, lx, ly, blue);
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                /*
                if (longClick == true) {
                    if (Math.abs(x - lx) > eps || Math.abs(y - ly) > eps) longClick = false;
                }
                */
                executeAction(1, x, y, blue);
                return true;
            case MotionEvent.ACTION_UP: {

                executeAction(2, event.getX(), event.getY(), blue); // ako ovo ne radi
                /*
                if (longClick) {
                    if ((System.currentTimeMillis() - firstTouch) / (double) 1000 > 0.7) {
                        layout.setVisibility(View.VISIBLE);
                        map.setTr(null);
                        longClick = false;
                    } else {
                        executeAction(2, event.getX(), event.getY(), blue);
                        longClick = false;
                    }
                } else executeAction(2, event.getX(), event.getY(), blue);
                */
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        height = blue.getHeight();
        width = blue.getWidth();
        showWidth = width * 4 / 5;
        blue.setMap(map);
    }

    @Override
    public void saveMap(String name) {
        if (name.isEmpty()) {
            DialogMessage dm = DialogMessage.newInstance("Map name must have at least one character.");
            dm.show(getFragmentManager(), "me");
        } else {
            File dir = Model.isRootPresent();
            File nf = new File(dir, name + ".txt");
            try {
                PrintWriter pw = new PrintWriter(nf);
                pw.write(map.getMapString(width, height));
                pw.flush();
                pw.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            saveMapImage(name);
            finish();
        }
    }

    protected void saveMapImage(String name) {
        File f = Model.isPicturePresent();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = blue.getBackground();
        if (drawable != null) drawable.draw(canvas);
        else canvas.drawColor(Color.WHITE);
        blue.draw(canvas);

        File nf = new File(f, name + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(nf);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BluePrintMap.BLUEPRINTMAP, map);
    }

}
