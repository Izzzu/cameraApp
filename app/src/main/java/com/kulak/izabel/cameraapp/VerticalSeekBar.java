package com.kulak.izabel.cameraapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    private static final String TAG = "VerticalSeekBar";
    private static int barLength = 0;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(90);
        c.translate(0, -getWidth());


        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setMax(barLength);

        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i=0;
                int v = (int) (getHeight() / event.getY());
                i=getMax() - (int) (getMax() * event.getY() / getHeight());
                Log.i("i", i + "");
                setProgress(getMax() - i);
                Log.i("Progress", getProgress() + "");
                Log.i("event.getY()", event.getY()+"");
                Log.i("getHeight()", getHeight() + "");
                Log.i("getMax()", getMax() + "");
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public static void setBarLength(int size) {
        barLength = size;
        Log.d(TAG, "BarLength: "+ barLength);
    }
}