package com.kulak.izabel.cameraapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DynamicImageView extends ImageView {

    private static final String TAG = "DynamicImageView";
    private Drawable drawable;

    public DynamicImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawable == null)
            return;

        setDimensionsMeasure(widthMeasureSpec, heightMeasureSpec);
        setScaleType(ScaleType.FIT_XY);
        setRotation();
    }

    private void setDimensionsMeasure(int widthMeasureSpec, int heightMeasureSpec) {
         drawable = getDrawable();
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        Log.d(TAG, "minimumWidth: " + minimumWidth);
        Log.d(TAG, "minimumHeight: "+ minimumHeight);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.d(TAG, "widthSpecMode: "+ widthSpecMode);
        Log.d(TAG, "heightSpecMode: " + heightSpecMode);
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Log.d(TAG, "w1: "+ w);
        Log.d(TAG, "h1: "+ h);
        if (w <= 0)
            w = 1;
        if (h <= 0)
            h = 1;

        // Desired aspect ratio of the view's contents (not including padding)
        float desiredAspect = (float) w / (float) h;
        Log.d(TAG, "w2: "+ w);
        Log.d(TAG, "h2: "+ h);

        // We are allowed to change the view's width
        boolean resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;

        // We are allowed to change the view's height
        boolean resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();
        Log.d(TAG, "pLeft "+ pleft);
        Log.d(TAG, "pRight "+ pright);
        Log.d(TAG, "pTop "+ ptop);
        Log.d(TAG, "pbottom "+ pbottom);

        // Get the sizes that ImageView decided on.
        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();

        // setMeasuredDimension(minimumHeight, minimumWidth);
        setParams(desiredAspect, resizeWidth, resizeHeight, pleft, pright, ptop, pbottom, widthSize, heightSize);
    }

    private void setParams(float desiredAspect, boolean resizeWidth, boolean resizeHeight, int pleft, int pright, int ptop, int pbottom, int widthSize, int heightSize) {
        if (resizeWidth && !resizeHeight) {
            // Resize the width to the height, maintaining aspect ratio.
            int newWidth = (int) (desiredAspect * (heightSize - ptop - pbottom)) + pleft + pright;
            Log.d(TAG, "newWidth " + newWidth);
            Log.d(TAG, "heightSize "+ heightSize);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newWidth, heightSize);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(-700);
            }
            setLayoutParams(params);

            setMeasuredDimension(newWidth, heightSize);
        } else if (resizeHeight && !resizeWidth) {
            int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect) + ptop + pbottom;
            Log.d(TAG, "widthSize "+ widthSize);
            Log.d(TAG, "newHeight "+ newHeight);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthSize, newHeight);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(-700);
            }
            setLayoutParams(params);
            setMeasuredDimension(widthSize, newHeight);
        }
    }

    private void setRotation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getDisplay().getRotation()== Surface.ROTATION_0) {
                setRotation(90);

            }
            if (getDisplay().getRotation()== Surface.ROTATION_180) {
                setRotation(270);

            }
        }
    }
}
