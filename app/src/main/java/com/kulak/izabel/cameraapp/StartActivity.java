package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_start);

        final Button button = (Button) findViewById(R.id.button_live_camera);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(StartActivity.this, CameraActivity.class);
                StartActivity.this.startActivity(changeActivity);
            }
        });
        final Button fancyButton = (Button) findViewById(R.id.button_run_fancy_camera);
        fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(StartActivity.this, FancyCameraActivity.class);
                StartActivity.this.startActivity(changeActivity);
            }
        });

    }

}
