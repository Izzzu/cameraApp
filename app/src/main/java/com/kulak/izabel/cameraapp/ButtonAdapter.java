package com.kulak.izabel.cameraapp;

import android.view.View;
import android.widget.Button;

/**
 * Created by i.kulakowska on 01/01/16.
 */
public interface ButtonAdapter {

    View.OnClickListener getOnClickListener(int position);
    void setButtonBackground(int position, Button button);
}
