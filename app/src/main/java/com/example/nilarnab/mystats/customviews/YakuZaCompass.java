package com.example.nilarnab.mystats.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.example.nilarnab.mystats.R;

/**
 * Created by nilarnab on 22/8/16 and it is made of each and everyone of you people to see, judge and advice :-).
 */
public class YakuZaCompass extends View {

    private float mRadius;
    private Paint mCirclePaint;

    public YakuZaCompass(Context context) {
        super(context);
        init();
    }

    public YakuZaCompass(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.YakuZaCompass,
                0, 0);

        try {
            mRadius = a.getDimension(R.styleable.YakuZaCompass_radius, 10);
        } finally {
            a.recycle();
        }
        init();
    }

    public YakuZaCompass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(this.getX(),this.getY(),mRadius,mCirclePaint);
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStrokeWidth(10);
    }
}
