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

import java.util.ArrayList;
import java.util.List;

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private static final String TAG = "ButtonAdapter";

    public ButtonAdapter(Context c) {
        mContext = c;
        backgroudColors = initializeBackGroundColors();
    }

    private List<Integer> initializeBackGroundColors() {
        List<ColorPick> colorPicks = new ColorsReader().getColorPicks();
        List<Integer> list = new ArrayList<>();
        for (ColorPick pick : colorPicks) {
            list.add(Color.rgb(pick.r, pick.g, pick.b));
        }
        return list;
    }

    public int getCount() {
        return backgroudColors.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Button getView(int position, View convertView, ViewGroup parent) {
        Button button;

        // if it's not recycled, initialize some attributes
        button = new Button(mContext);
        // button.setLayoutParams(new GridView.LayoutParams(80, 80));
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.color_button, mContext.getApplicationContext().getTheme());
        } else {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.color_button);
        }

        button.setBackground(drawable);
        GradientDrawable draw = (GradientDrawable) button.getBackground();
        draw.setColor(backgroudColors.get(position));
        //button.setPadding(8, 8, 8, 8);

        return button;
    }


    // references to our images
    private List<Integer> backgroudColors = new ArrayList<>();
}