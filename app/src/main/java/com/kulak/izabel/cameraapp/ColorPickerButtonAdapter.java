package com.kulak.izabel.cameraapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerButtonAdapter extends BaseAdapter implements ButtonAdapter {
    // references to our images
    private static List<Scalar> backgroundColors = new ArrayList<>();
    private ColorPickerFragment colorPickerFragment;
    private Context mContext;
    private static final String TAG = "ColorPickerButtonAdapter";
    public static Scalar currentColor;



    public List<Scalar> lastUsedColors = new ArrayList<>();

    public ColorPickerButtonAdapter(Context c,  ColorPickerFragment colorPickerFragment ) {
        mContext = c;
        backgroundColors = initializeBackGroundColors();
        this.colorPickerFragment = colorPickerFragment;
    }

    public ColorPickerButtonAdapter(Context c,  List<Scalar> backgroundColors) {
        mContext = c;
        this.backgroundColors = backgroundColors;
    }

    public ColorPickerButtonAdapter(Context c) {
        mContext = c;
    }

    public List<Scalar> initializeBackGroundColors() {
        List<ColorPick> colorPicks = new ColorsReader().getColorPicks();
        List<Scalar> list = new ArrayList<>();
        for (ColorPick pick : colorPicks) {
            list.add(new Scalar((int) pick.r, (int) pick.g, (int) pick.b));
        }
        return list;
    }

    public int getCount() {
        return backgroundColors.size();
    }

    public Object getItem(int position) {
        return backgroundColors.get(position);
    }

    public long getItemId(int position) {
        return backgroundColors.get(position).hashCode();
    }

    public View.OnClickListener getOnClickListener(final int position) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = backgroundColors.get(position);
                ColorPickerFragment.setLastPicked(currentColor);
                colorPickerFragment.updateLastPickedColors();
                colorPickerFragment.closeDrawer();
            }
        };
        return onClickListener;
    }

    // create a new ImageView for each item referenced by the Adapter
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Button getView(final int position, View convertView, ViewGroup parent) {
        Button button;

        // if it's not recycled, initialize some attributes
        button = new Button(mContext);
        setButtonBackground(position, button);

        button.setOnClickListener(getOnClickListener(position));
        //button.setPadding(8, 8, 8, 8);
        return button;
    }


    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setButtonBackground(int position, Button button) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.color_button, mContext.getApplicationContext().getTheme());
        } else {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.color_button);
        }
        GradientDrawable draw = (GradientDrawable) drawable;
        currentColor = backgroundColors.get(position);

        draw.setColor(Color.rgb((int) currentColor.val[0], (int) currentColor.val[1], (int) currentColor.val[2]));

        button.setBackground(draw);
    }

    public static Scalar getLastPicked() {
        return currentColor;
    }
}