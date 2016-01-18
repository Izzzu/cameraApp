package com.kulak.izabel.cameraapp.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.kulak.izabel.cameraapp.ColorMoldListItem;
import com.kulak.izabel.cameraapp.MoldItemListAdapter;
import com.kulak.izabel.cameraapp.R;
import com.kulak.izabel.cameraapp.VerticalSeekBar;

import java.util.ArrayList;

public class MoldListActivity extends Activity {

    private VerticalSeekBar verticalSeekBar;
    private ListView listOfMolds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mold_list_activity);

        verticalSeekBar = (VerticalSeekBar) findViewById(R.id.seekBar);
        listOfMolds = (ListView) findViewById(R.id.mold_list);

        ArrayList<ColorMoldListItem> moldListItems = new ArrayList<>();
        populateColorMoldListItems(moldListItems);


        MoldItemListAdapter adapter = new MoldItemListAdapter(getApplicationContext(), moldListItems);
        listOfMolds.setAdapter(adapter);

        
    }

    private void populateColorMoldListItems(ArrayList<ColorMoldListItem> moldListItems) {
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
        moldListItems.add(new ColorMoldListItem());
    }
}
