package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.kulak.izabel.cameraapp.activity.ColorBlobDetectionActivity;

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
    private int layout_id;

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;

    public ColorPickerFragment(int layout_id, final Activity activity) {
        super();
        this.layout_id = layout_id;
        mDrawerLayout = (DrawerLayout) activity.findViewById(layout_id);
        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ((ColorPickerOwner)activity).openColorPickerFragment();
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void changedMenuConfiguration(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void synchronizeMenuState() {
        mDrawerToggle.syncState();
    }

    public ColorPickerFragment() {
        super();
    }

    public static synchronized void setLastPicked(Scalar lastPicked) {
        ColorPickerFragment.lastPicked = lastPicked;
    }

    public ActionBarDrawerToggle getmDrawerToggle() {
        return mDrawerToggle;
    }

    public void setmDrawerToggle(ActionBarDrawerToggle mDrawerToggle) {
        this.mDrawerToggle = mDrawerToggle;
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
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

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.END);
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



