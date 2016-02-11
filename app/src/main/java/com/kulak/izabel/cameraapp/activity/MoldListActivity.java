package com.kulak.izabel.cameraapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SeekBar;

import com.kulak.izabel.cameraapp.ColorMoldListItem;
import com.kulak.izabel.cameraapp.MoldItemListAdapter;
import com.kulak.izabel.cameraapp.R;
import com.kulak.izabel.cameraapp.VerticalSeekBar;

import java.util.ArrayList;

public class MoldListActivity extends Activity {

    private static final String TAG = "MoldListActivity";
    private VerticalSeekBar verticalSeekBar;
    private ListView listOfMolds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mold_list_activity);

        listOfMolds = (ListView) findViewById(R.id.mold_list);
        listOfMolds.setScrollContainer(false);
        listOfMolds.setClickable(false);
        listOfMolds.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int firstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState==0) {
                    if (verticalSeekBar!=null) {
                        int progress = verticalSeekBar.getProgress();
                        Log.d(TAG, "VerticalSeekBar, progress: "+progress);
                        Log.d(TAG, "VertfirstVisibleItem: "+firstVisibleItem);
                        if (firstVisibleItem != progress) {
                            //verticalSeekBar.scroll(firstVisibleItem);
                            Log.d(TAG, "firstVisible item not eqial to progress !!!!!!!!!!!!!!");
                        }
                    }
            }}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                if (verticalSeekBar!=null) {
                    int progress = verticalSeekBar.getProgress();
                    Log.d(TAG, "VerticalSeekBar, progress: "+progress);
                    Log.d(TAG, "VertfirstVisibleItem: "+firstVisibleItem);
                    if (firstVisibleItem != progress) {
                        //verticalSeekBar.scroll(firstVisibleItem);
                    }
                }
            }
        });
        ArrayList<ColorMoldListItem> moldListItems = populateColorMoldListItems();

        setupVerticalSeekBar(moldListItems.size());

        MoldItemListAdapter adapter = new MoldItemListAdapter(getApplicationContext(), moldListItems);
        listOfMolds.setAdapter(adapter);
    }

    private void setupVerticalSeekBar(int size) {
        VerticalSeekBar.setBarLength(size);
        verticalSeekBar = (VerticalSeekBar) findViewById(R.id.seekBar);
        verticalSeekBar.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(size));
    }

    private ArrayList<ColorMoldListItem> populateColorMoldListItems() {
        ArrayList<ColorMoldListItem> moldListItems = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            moldListItems.add(new ColorMoldListItem());
        }
        return moldListItems;
    }

    class CustomOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        int sizeOfColors;
        public CustomOnSeekBarChangeListener(int sizeOfColors) {
            this.sizeOfColors = sizeOfColors;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int position = getPosition(progress);
                listOfMolds.smoothScrollToPositionFromTop(position, 0);
        }

        private int getPosition(int progress) {
            return (progress >= sizeOfColors && sizeOfColors > 0) ? sizeOfColors - 1 : progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
