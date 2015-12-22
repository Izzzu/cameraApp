package com.kulak.izabel.cameraapp;


import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FancyCameraView extends JavaCameraView implements Camera.PictureCallback {

    private static final String TAG = "FancyCamera";
    private String imagePath;
    private SurfaceHolder mHolder;

    public List<Camera.Size> getmSupportedPreviewSizes() {
        return mSupportedPreviewSizes;
    }

    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;


    public FancyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
         }

    public FancyCameraView(Context context, int cameraId) {
        super(context, cameraId);
        // supported preview sizes
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);
        mHolder = getHolder();
       // Log.d(TAG, "mHolder "+mHolder);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected boolean initializeCamera(int w, int h) {

        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d(TAG, "INITIALIZE CAMERA width: " + w + ", height: " + h);
        Log.d(TAG, "INITIALIZE CAMERA FROM DISPLAY width: " + width + ", height: " + height);


        boolean b = super.initializeCamera(width, height);
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);
        mHolder = getHolder();
        Log.d(TAG, "mHolder "+mHolder);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return b;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize =
                    getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
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

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
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
