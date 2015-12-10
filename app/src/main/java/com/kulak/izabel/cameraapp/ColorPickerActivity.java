package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorPickerActivity extends Activity {

    ArrayList<String> items = new ArrayList<String>();
    ListView topListview;
    ArrayAdapter mAdapter;
    String[] titles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        topListview = (ListView) findViewById(R.id.colors_group_list);
        titles = getResources().getStringArray(R.array.colors_group_titles);

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < titles.length; ++i) {
            list.add(titles[i]);
        }
        mAdapter = new ArrayAdapter(getApplicationContext(), R.layout.color_group_list_item, R.id.color_group_list_item_text, list);
        topListview.setAdapter(mAdapter);

        topListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }

        });

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(this.getApplicationContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            }
        });
    }

    }



