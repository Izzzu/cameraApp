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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorPickerFragment extends Fragment {

    private static final String TAG = "ColorPickerFragment";
    ArrayList<String> items = new ArrayList<String>();
    ListView topListview;
    ArrayAdapter mAdapter;
    String[] titles;

    public static Scalar lastPicked = null;
    private LastUsedColorsButtonAdapter lastUsedColorsButtonAdapter;
    private GridView gridview;
    private GridView gridViewLastUsedColors;
    private View view;

    public static synchronized void setLastPicked(Scalar lastPicked) {
        ColorPickerFragment.lastPicked = lastPicked;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.activity_color_picker, container, false);

        initTopList(view);
        initGridWithColorButtons(view);

        initGridWithLastUsedColors(view);

        return view;

    }



    private void initGridWithLastUsedColors(View view) {
        gridViewLastUsedColors = (GridView) view.findViewById(R.id.gridview_last_used_colors);
        lastUsedColorsButtonAdapter = new LastUsedColorsButtonAdapter(getActivity().getApplicationContext(), this);
        gridViewLastUsedColors.setAdapter(lastUsedColorsButtonAdapter);
    }

    private void initGridWithColorButtons(View view) {
       gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ColorPickerButtonAdapter(getActivity().getApplicationContext(), this));
    }

    private void initTopList(View view) {
        topListview = (ListView) view.findViewById(R.id.colors_group_list);

        titles = getResources().getStringArray(R.array.colors_group_titles);

        mAdapter = new ArrayAdapter(getActivity(), R.layout.color_group_list_item, R.id.color_group_list_item_text, Arrays.asList(titles));
        topListview.setAdapter(mAdapter);

        topListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }

        });
    }

    public static synchronized Scalar getLastPicked() {
        return lastPicked;
    }

    public void closeColorPicker() {
        ((ColorBlobDetectionActivity)getActivity()).closeRightPaneIfItIsOpen();

    }

    protected IActivityEnabledListener aeListener;

    public void updateLastPickedColors() {
        Log.d(TAG, "invalidate grid");
        lastUsedColorsButtonAdapter.addToLastUsedColors(lastPicked);
        lastUsedColorsButtonAdapter.notifyDataSetChanged();
        //initGridWithLastUsedColors(view);
        gridViewLastUsedColors.invalidateViews();
        //view.invalidate();
        Log.d(TAG, "invalidate all");
    }

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



