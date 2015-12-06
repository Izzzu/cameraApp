package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;


public class LeftMenu {
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
    }

    void initializeMenuListResources(Resources resources) {
        titles = resources.getStringArray(R.array.nav_drawer_items);
        icons = resources.obtainTypedArray(R.array.nav_drawer_icons);
    }

    void initializeMenuItems(Activity photoActivity) {
        LinearLayout photography_wizualize_item = (LinearLayout) photoActivity.findViewById(R.id.photography_wizualize_item);
        photography_wizualize_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        LinearLayout live_wizualize_item = (LinearLayout) photoActivity.findViewById(R.id.live_wizualize_item);
        live_wizualize_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    void setOnItemClickListener() {
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
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
                //getActionBar().setTitle("Navigation!");
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mActivityTitle);
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }

    void initializeLeftMenu(Resources resources, Context context, Activity activity) {
        findLeftMenuElementsInLayout(activity);

        initializeMenuListResources(resources);
        initializeDrawerItems();
        initializeMenuItems(activity);

        recycleIcons();
        setOnItemClickListener();
        settingUpAdapter(context);
        setUpDrawer(activity);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    void changedLeftMenuConfiguration(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    void synchronizeLeftMenuState() {
        mDrawerToggle.syncState();
    }


    /**
     * Slide menu item click listener *
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Display appropriate fragment for selected item
            displayView(position);

        }
    }


    private void displayView(int position) {
        switch (position) {
            case 0:
// Update selected item and title, then close the drawer
                closeDrawer(position);
                break;
            case 1:
                closeDrawer(position);
                break;
            case 2:
                closeDrawer(position);
                break;
            case 3:
                closeDrawer(position);
                break;
            default:
                break;
        }
    }

}