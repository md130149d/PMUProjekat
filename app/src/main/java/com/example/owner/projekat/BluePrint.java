package com.example.owner.projekat;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BluePrint extends ImageView {

    protected BluePrintMap map;
    protected Initializer initializer;

    public BluePrint(Context context) {
        super(context);
    }

    public BluePrint(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BluePrint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BluePrint(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMap(BluePrintMap map) {
        this.map = map;
    }

    public void setInitializer(Initializer initializer){ this.initializer=initializer; }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(map!=null){
            map.draw(canvas);
        } else if(initializer!=null){
            initializer.init();
            map.draw(canvas);
        }
    }

    public interface Initializer{
        void init();
    }
}
