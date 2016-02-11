package com.kulak.izabel.cameraapp.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kulak.izabel.cameraapp.ColorPickerButtonAdapter;
import com.kulak.izabel.cameraapp.R;

import org.opencv.core.Scalar;

import java.util.LinkedList;

public class ColorHintFragment extends Fragment{

    private LinkedList<Scalar> backgroundColors = new LinkedList<Scalar>();
    private GridView gridview;
    private View view;


    /*public ColorHintFragment(final Activity activity, LinkedList<Scalar> backgroundColors) {

        this.backgroundColors = backgroundColors;
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        backgroundColors.add(new Scalar(250, 240, 120));
        //gridview.setAdapter(new ColorPickerButtonAdapter(getActivity().getApplicationContext(), backgroundColors));
    }*/

    public ColorHintFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.color_hint_fragment, container, false);
        gridview = (GridView)view.findViewById(R.id.gridview);
        gridview.setAdapter(new ColorPickerButtonAdapter(getActivity().getApplicationContext(), backgroundColors));

        return view;
    }

}
