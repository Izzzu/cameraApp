package com.kulak.izabel.cameraapp.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.kulak.izabel.cameraapp.LeftMenu;
import com.kulak.izabel.cameraapp.R;

public class CalculatorActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private int width = 0;
    private int height = 0;
    private int noSurfaces = 0;
    private EditText widthEditText;
    private EditText heightEditText;
    private EditText noSurfacesEditText;
    LeftMenu leftMenu = new LeftMenu(R.id.drawer_layout_calculator_activity);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_activity);
        initSpinners();
        initEditTexts();
        leftMenu.initializeLeftMenu(getResources(), getApplicationContext(), this);
        ImageButton settings = (ImageButton) findViewById(R.id.settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftMenu.openDrawer();
            }
        });
    }

    private void initEditTexts() {
        widthEditText = (EditText) findViewById(R.id.editText);
        heightEditText = (EditText) findViewById(R.id.editText2);
        noSurfacesEditText = (EditText) findViewById(R.id.editText3);
    }

    private void initSpinners() {
        initSpinner(R.id.spinner, R.array.spinner_ext_or_int);
        initSpinner(R.id.spinner2, R.array.spinner_type_of_surface);
        initSpinner(R.id.spinner3, R.array.spinner_paint_type);
    }

    private void initSpinner(int spinnerId, int spinner_items) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                spinner_items, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter3);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMenu.synchronizeLeftMenuState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
    }
}
