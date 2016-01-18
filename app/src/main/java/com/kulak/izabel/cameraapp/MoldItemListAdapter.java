package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class MoldItemListAdapter extends BaseAdapter{

    private static final String TAG = "MoldItemListAdapter";
    private Context context;
    private ArrayList<ColorMoldListItem> moldListItems = new ArrayList<>();

    public MoldItemListAdapter(Context context, ArrayList<ColorMoldListItem> moldListItems) {
        this.context = context;
        this.moldListItems = moldListItems;
        Log.d(TAG, "moldListItem: "+ moldListItems.get(0));
    }

    @Override
    public int getCount() {
        return moldListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return moldListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return moldListItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "In getView - Mold List");
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.mold_list_item, null);
        }
        TextView colorView = (TextView) convertView.findViewById(R.id.mold_item_color);
        TextView nameColorView = (TextView) convertView.findViewById(R.id.mold_item_name);
        TextView groupColorView = (TextView) convertView.findViewById(R.id.mold_item_group);
        ColorMoldListItem colorMoldListItem = moldListItems.get(position);
        Log.d(TAG, "colorMoldListItem: position" + position + " -" + colorMoldListItem.name + " ," + colorMoldListItem.color);
        colorView.setBackgroundColor(colorMoldListItem.color);
        nameColorView.setText(colorMoldListItem.name);
        groupColorView.setText(colorMoldListItem.group);
        return convertView;
    }
}
