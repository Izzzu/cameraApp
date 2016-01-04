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

public class LastUsedColorsButtonAdapter extends BaseAdapter implements ButtonAdapter {

    private static final String TAG = "LastUsedColorsAdapter";
    private Context mContext;
    private ColorPickerFragment colorPickerFragment;
    private static List<Scalar> lastUsedColors;
    private LastUsedColorsButtonAdapter adapter = this;
    private Scalar currentColor;
    private static final int LAST_USED_COLORS_IN_ROW = 5;

    public LastUsedColorsButtonAdapter(Context c, ColorPickerFragment colorPickerFragment) {
        super();
        mContext = c;
        this.colorPickerFragment = colorPickerFragment;
        lastUsedColors = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return lastUsedColors.size();
    }

    @Override
    public Object getItem(int position) {
        return lastUsedColors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lastUsedColors.get(position).hashCode();
    }

    @Override
    public View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = lastUsedColors.get(position);
                ColorPickerFragment.setLastPicked(currentColor);
                colorPickerFragment.updateLastPickedColors();
                colorPickerFragment.closeDrawer();
            }
        };
    }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Button getView(final int position, View convertView, ViewGroup parent) {

        Button button;
            button = new Button(mContext);
            setButtonBackground(position, button);
            button.setOnClickListener(getOnClickListener(position));
        return button;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setButtonBackground(int position, Button button) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.last_used_color_button, mContext.getApplicationContext().getTheme());
        } else {
            drawable = (Drawable) mContext.getResources().getDrawable(R.drawable.last_used_color_button);
        }
        GradientDrawable draw = (GradientDrawable) drawable;
        currentColor = lastUsedColors.get(position);
        draw.setColor(Color.rgb((int) currentColor.val[0], (int) currentColor.val[1], (int) currentColor.val[2]));
        button.setBackground(draw);
    }

    public void addToLastUsedColors(Scalar currentColor) {
        lastUsedColors.add(currentColor);
        if (lastUsedColors.size()> LAST_USED_COLORS_IN_ROW) {
            for (int i = 0; i < lastUsedColors.size()- LAST_USED_COLORS_IN_ROW; i++) {
                lastUsedColors.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
