package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.opencv.core.Scalar;

import java.util.ArrayList;

public class ColorPickerFragment extends Fragment {

    private static final String TAG = "ColorPickerFragment";
    ArrayList<String> items = new ArrayList<String>();
    ListView topListview;
    ArrayAdapter mAdapter;
    String[] titles;

    public static Scalar lastPicked = null;

    public static synchronized void setLastPicked(Scalar lastPicked) {
        ColorPickerFragment.lastPicked = lastPicked;
    }


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

        return view;

    }

    public static synchronized Scalar getLastPicked() {
        return lastPicked;
    }

    public void closeColorPicker() {
        ((ColorBlobDetectionActivity)getActivity()).closeRightPaneIfItIsOpen();

    }


    protected IActivityEnabledListener aeListener;

    protected interface IActivityEnabledListener{
        void onActivityEnabled(FragmentActivity activity);
    }

    protected void getAvailableActivity(IActivityEnabledListener listener){
        if (getActivity() == null){
            aeListener = listener;

        } else {
            listener.onActivityEnabled((FragmentActivity) getActivity());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (aeListener != null){
            aeListener.onActivityEnabled((FragmentActivity) activity);
            aeListener = null;
        }
    }


}



