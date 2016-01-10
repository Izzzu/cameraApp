package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kulak.izabel.cameraapp.activity.CalculatorActivity;

import java.util.ArrayList;


public class LeftMenu {
    private static final String TAG = "Left menu";
    ActionBarDrawerToggle mDrawerToggle;

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
    NavDrawerListAdapter mAdapter;
    String[] titles;
    TypedArray icons;
    private int layout_id;

    public LeftMenu(int layout_id) {
        this.layout_id = layout_id;
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

    public ListView getmDrawerList() {
        return mDrawerList;
    }

    public void setmDrawerList(ListView mDrawerList) {
        this.mDrawerList = mDrawerList;
    }

    public ArrayList<NavDrawerItem> getNavDrawerItems() {
        return navDrawerItems;
    }

    public void setNavDrawerItems(ArrayList<NavDrawerItem> navDrawerItems) {
        this.navDrawerItems = navDrawerItems;
    }

    public NavDrawerListAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(NavDrawerListAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public TypedArray getIcons() {
        return icons;
    }

    public void setIcons(TypedArray icons) {
        this.icons = icons;
    }

    void initializeDrawerItems() {
        navDrawerItems.add(new NavDrawerItem(titles[0], icons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(titles[1], icons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(titles[2], icons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(titles[3], icons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(titles[4], icons.getResourceId(4, -1)));
    }

    void initializeMenuListResources(Resources resources) {
        titles = resources.getStringArray(R.array.nav_drawer_items);
        icons = resources.obtainTypedArray(R.array.nav_drawer_icons);
    }

    void initializeMenuItems(final Activity activity) {
        LinearLayout photography_wizualize_item = (LinearLayout) activity.findViewById(R.id.photography_wizualize_item);
        photography_wizualize_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent changeActivity = new Intent(activity, PhotoActivity.class);
                activity.startActivity(changeActivity);
                mDrawerLayout.closeDrawers();

            }
        });
        LinearLayout live_wizualize_item = (LinearLayout) activity.findViewById(R.id.live_wizualize_item);
        live_wizualize_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(activity, ColorBlobDetectionActivity.class);
                activity.startActivity(changeActivity);
                mDrawerLayout.closeDrawers();
            }
        });
    }

    void settingUpAdapter(Context applicationContext) {
        mAdapter = new NavDrawerListAdapter(applicationContext, navDrawerItems);
        mDrawerList.setAdapter(mAdapter);
    }

    private void closeDrawer(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawers();

    }

    void setOnItemClickListener(Activity activity) {
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener(activity));
    }

    void recycleIcons() {
        icons.recycle();
    }

    void findLeftMenuElementsInLayout(Activity activity) {
        mDrawerList = (ListView) activity.findViewById(R.id.nav_menu);
        mDrawerLayout = (DrawerLayout) activity.findViewById(layout_id);
    }

    void setUpDrawer(final Activity activity) {
        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {


            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // closeDrawer(GravityCompat.END);
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
    }

    public void initializeLeftMenu(Resources resources, Context context, Activity activity) {
        findLeftMenuElementsInLayout(activity);

        initializeMenuListResources(resources);
        initializeDrawerItems();
        initializeMenuItems(activity);

        recycleIcons();
        setOnItemClickListener(activity);
        settingUpAdapter(context);
        setUpDrawer(activity);

        if(mDrawerToggle == null) {
            Log.d(TAG, "mDrawerToggle is null");
        }

        if(mDrawerLayout == null) {
            Log.d(TAG, "mDrawerLayout is null");
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void changedLeftMenuConfiguration(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void synchronizeLeftMenuState() {
        mDrawerToggle.syncState();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Slide menu item click listener *
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        private final Activity activity;

        public SlideMenuClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Display appropriate fragment for selected item
            displayView(position, activity);

        }
    }

    private void displayView(int position, Activity activity) {
        Intent changeActivity;
        switch (position) {
            case 0:
                closeDrawer(position);
                break;
            case 1:
                closeDrawer(position);
                break;
            case 2:
                changeActivity = new Intent(activity, MapActivity.class);
                activity.startActivity(changeActivity);
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                closeDrawer(position);
                break;
            case 4:
                changeActivity = new Intent(activity, CalculatorActivity.class);
                activity.startActivity(changeActivity);
                mDrawerLayout.closeDrawers();
                break;
            default:
                break;
        }
    }

}