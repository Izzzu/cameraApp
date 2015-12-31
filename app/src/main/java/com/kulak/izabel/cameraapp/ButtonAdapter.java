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

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private static final String TAG = "ButtonAdapter";
    private static Scalar scalar;

    public ButtonAdapter(Context c) {
        mContext = c;
        backgroundColors = initializeBackGroundColors();
    }

    private List<Scalar> initializeBackGroundColors() {
        List<ColorPick> colorPicks = new ColorsReader().getColorPicks();
        List<Scalar> list = new ArrayList<>();
        for (ColorPick pick : colorPicks) {
            list.add(new Scalar((int)pick.r, (int)pick.g, (int)pick.b));
        }
        return list;
    }

    public int getCount() {
        return backgroundColors.size();
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
        scalar = backgroundColors.get(position);
        draw.setColor(Color.rgb((int)scalar.val[0],(int)scalar.val[1], (int)scalar.val[2]) );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerActivity.setLastPicked(scalar);
            }
        });
        //button.setPadding(8, 8, 8, 8);
        return button;
    }


    // references to our images
    private static List<Scalar> backgroundColors = new ArrayList<>();

    public static Scalar getLastPicked() {
        return scalar;
    }
}