package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;

    private View.OnClickListener cameraPictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_LONG).show();
            mCamera.startPreview();
        }
    };

    private static int picNr = 0;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File photoDir = getPictureDir();
            if (photoDir == null) {
                return;
            }
            File pictureFile = new File(photoDir.getPath() + File.separator + "PICTURE_" + picNr + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    @Nullable
    private File getPictureDir() {
        File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture_" + picNr + ".jpg");
        if (!photoDir.exists()) {
            if (!photoDir.mkdirs()) {
                Log.d("CameraApp", "failed to create directory");
                return null;
            }
        }
        return photoDir;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCamera = getCameraInstance();

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);
        List<Camera.Size> sizes = mPreview.getmSupportedPreviewSizes();
        Point displaySize = new Point();
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.getWindowManager().getDefaultDisplay().getRealSize(displaySize);
            width = displaySize.x;
            height = displaySize.y;
        } else {
            DisplayMetrics metrics = this.getBaseContext().getResources().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        }

        Log.d("Camera", "sizes: " + sizes.toString());
        Log.d("Camera", "width: " + width);
        Log.d("Camera", "height: " + height);
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(sizes, width, height);

        Log.d("Camera", "optimal width: " + optimalPreviewSize.width);
        Log.d("Camera", "optimal height: " + optimalPreviewSize.height);
        mPreview.setLayoutParams(new FrameLayout.LayoutParams(optimalPreviewSize.width, optimalPreviewSize.height));
        Button cameraButton = (Button) findViewById(R.id.button_capture);
        cameraButton.setOnClickListener(cameraPictureListener);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("Camera", "There is no action bar");
        }

        final TextView textView = (TextView)findViewById(R.id.touch_coordinates);
        // this is the view on which you will listen for touch events
        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.setText("Touch coordinates : " +
                        String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                return true;
            }
        });

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
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

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();

        } catch (Exception e) {
            Log.d("Camera", "Camera is not available");
            e.printStackTrace();
        }
        return camera;
    }
}
