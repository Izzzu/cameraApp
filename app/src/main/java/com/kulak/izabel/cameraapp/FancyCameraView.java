package com.kulak.izabel.cameraapp;


import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.io.IOException;

public class FancyCameraView extends JavaCameraView implements Camera.PictureCallback {

    private static final String TAG = "FancyCamera";
    private String imagePath;

    public FancyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FancyCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    @Override
    protected boolean initializeCamera(int w, int h) {

        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d(TAG, "INITIALIZE CAMERA width: "+ w + ", height: "+ h);
        Log.d(TAG, "INITIALIZE CAMERA FROM DISPLAY width: "+ width + ", height: "+ height);
        return super.initializeCamera(width, height);

    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.i(TAG, "Saving a bitmap to file");

        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        try {
            FileOutputStream outputStream = new FileOutputStream(imagePath);
            outputStream.close();
            outputStream.write(data);
        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);

            e.printStackTrace();
        }

    }


    public void takePicture(String fileName) {
        Log.i(TAG, "Take picture");

        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        imagePath = fileName;
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);
    }
}
