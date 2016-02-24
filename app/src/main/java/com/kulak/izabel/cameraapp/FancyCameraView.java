package com.kulak.izabel.cameraapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import org.opencv.android.BetterJavaCameraView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FancyCameraView extends BetterJavaCameraView implements Camera.PictureCallback {

    private static final String TAG = "FancyCamera";
    private String imagePath;
    private SurfaceHolder mHolder;
    private boolean isPreviewRunning;

    public List<Camera.Size> getmSupportedPreviewSizes() {
        return mSupportedPreviewSizes;
    }

    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    OrientationEventListener mOrientationListener;



    public FancyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Log.d(TAG, "Tutaj3");
        //Log.d(TAG, "Tutaj1");

        //for(Camera.Size str: mSupportedPreviewSizes)
         //   Log.e(TAG, str.width + "/" + str.height);
        mHolder = getHolder();
        // Log.d(TAG, "mHolder "+mHolder);
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public FancyCameraView(Context context, int cameraId) {
        super(context, cameraId);
        // supported preview sizes
        mSupportedPreviewSizes = getSupportedPreviewSizes();
        Log.d(TAG, "Tutaj1");

        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);
        mHolder = getHolder();
       // Log.d(TAG, "mHolder "+mHolder);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private List<Camera.Size> getSupportedPreviewSizes() {
        List<int[]> supportedPreviewFpsRange = mCamera.getParameters().getSupportedPreviewFpsRange();
        for (int[] i : supportedPreviewFpsRange) {
            Log.d("CameraApp", "Supported preview fp range: ");
            for (int k = 0; k<i.length; k++) {
                Log.d("CameraApp", "fp range: " + i[k]);
            }
        }
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
        for (Integer a : supportedPreviewFrameRates) {
            Log.d("CameraApp", "supportedPreviewFrameRates: " + a);
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    /*public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        super.surfaceChanged(holder, format, width, height);
        Log.d(TAG, "surface changed started ");

        if (isPreviewRunning)
        {
            Log.d(TAG, "preview is running ");

            mCamera.stopPreview();
        }
        Log.d(TAG, "Tutaj2");
        Log.d(TAG, "In surface changed: height/width " + height + "," + width);

        Camera.Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        List<Camera.Size> sizes = getmSupportedPreviewSizes();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            Log.d(TAG, "--->0");

            updateCameraSupportedSizeAndRotation(width, height, parameters, sizes, 90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            Log.d(TAG, "--->90");

            updateCameraSupportedSizeAndRotation(height, width, parameters, sizes, 0);
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            Log.d(TAG, "--->180");

            updateCameraSupportedSizeAndRotation(width, height, parameters, sizes, 0);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            Log.d(TAG, "--->270");

            updateCameraSupportedSizeAndRotation(height, width, parameters, sizes, 180);

        }
        previewCamera();
    }*/

    private void updateCameraSupportedSizeAndRotation(int width, int height, Camera.Parameters parameters, List<Camera.Size> sizes, int degrees) {
        Camera.Size supportedSize = calculateSupportedSize(width, height, sizes);
        parameters.setPreviewSize(supportedSize.width, supportedSize.height);
        mCamera.setDisplayOrientation(degrees);
        Log.d(TAG, "SUPPORTED SIZE: w/h " + supportedSize.width + ", " + supportedSize.height);
        mCamera.setParameters(parameters);
    }

    @Nullable
    private Camera.Size calculateSupportedSize(int height, int width, List<Camera.Size> sizes) {
        Camera.Size supportedSize = null;
        for (Camera.Size size : sizes) {
            if (size.height == height) {
                supportedSize = size;
            }
        }
        if (supportedSize == null) {
            for (Camera.Size size : sizes) {
                if (size.width == width) {
                    supportedSize = size;
                }
            }
        }
        return supportedSize;
    }


    public void onPause() {
        mCamera.release();
        mCamera = null;
    }

    private boolean DEBUGGING = true;
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";


    public void previewCamera()
    {
        try
        {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);
        }
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
        /* First, get the Display from the WindowManager */

/* Now we can retrieve all display-related infos */
         int rotation = wm.getDefaultDisplay().getRotation();
        Log.d(TAG, "Rotation: "+rotation);

        //mCamera.setDisplayOrientation(90);
        //mCamera.setDisplayOrientation(90);
        mSupportedPreviewSizes = getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e(TAG, str.width + "/" + str.height);

        //mHolder = getHolder();
        Log.d(TAG, "mHolder " + mHolder);
        Log.d(TAG, "initialize finished ");
        /*Camera.Parameters parameters = mCamera.getParameters();
        int rangebefore[] = new int[2];
        parameters.getPreviewFpsRange(rangebefore);*/
            //parameters.setPreviewFrameRate(30);
            //mCamera.setParameters(parameters);
        /*int range[] = new int[2];
        mCamera.getParameters().getPreviewFpsRange(range);
        Log.d(TAG, "fps range: --- " + range[0] + "," + range[1]);*/

        //mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return b;

    }

    /*//
    // Native JNI
    //
    public native boolean ImageProcessing(int width, int height,
                                          byte[] NV21FrameData, int [] pixels);
    static
    {
        System.loadLibrary("ImageProcessing");
    }*/

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else
        { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
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

   /* @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        super.surfaceChanged(holder, format, width, height);
        //if (mCamera!=null) mCamera.setDisplayOrientation(90);
    }*/


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

    public Canvas lockCanvas() {
        return mHolder.lockCanvas();
    }

    public void unlockCanvas(Canvas c) {
        mHolder.unlockCanvasAndPost(c);
    }
}
