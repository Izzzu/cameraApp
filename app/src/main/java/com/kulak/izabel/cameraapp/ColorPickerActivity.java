package com.kulak.izabel.cameraapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorPickerActivity extends Fragment {

    private static final String TAG = "ColorPickerActivity";
    ArrayList<String> items = new ArrayList<String>();
    ListView topListview;
    ArrayAdapter mAdapter;
    String[] titles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_color_picker, container, false);

        topListview = (ListView) view.findViewById(R.id.colors_group_list);
        Log.d(TAG,"topListview: "+topListview);
        titles = getResources().getStringArray(R.array.colors_group_titles);

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < titles.length; ++i) {
            list.add(titles[i]);
        }
        Log.d(TAG, "getActivity: "+getActivity());
        Log.d(TAG, "list: "+list.size());
        mAdapter = new ArrayAdapter(getActivity(), R.layout.color_group_list_item, R.id.color_group_list_item_text, list);
        topListview.setAdapter(mAdapter);

        topListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }

        });

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(getActivity().getApplicationContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            }
        });

        return view;

    }
}



