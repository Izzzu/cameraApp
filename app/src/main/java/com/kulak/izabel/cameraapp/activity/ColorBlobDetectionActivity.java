package com.kulak.izabel.cameraapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kulak.izabel.cameraapp.ColorBlobDetector;
import com.kulak.izabel.cameraapp.ColorPickerFragment;
import com.kulak.izabel.cameraapp.ColorPickerOwner;
import com.kulak.izabel.cameraapp.FancyCameraView;
import com.kulak.izabel.cameraapp.LeftMenu;
import com.kulak.izabel.cameraapp.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

public class ColorBlobDetectionActivity extends FragmentActivity implements View.OnTouchListener, CvCameraViewListener2, SensorEventListener, ColorPickerOwner {
    private static final String  TAG              = "OCVSample::Activity";
    private static final int THRESHOLD = 15;

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    private FancyCameraView mOpenCvCameraView;

    private LeftMenu leftMenu = new LeftMenu(R.id.drawer_layout_blob_detection_activity);

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private Point touched = null;
    public static boolean COLOR_PICKER_ON;
    private Scalar pickedColor = null;
    private ImageView currentColorView;
    private DrawerLayout rightDrawerLayout;
    private ColorPickerFragment rightFragment;
    private List<Point> listOfPoints = new LinkedList<>();
    private float[] mRotationMatrix = new float[9];
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean added;
    private float[] inMemoryOrientationVals = new float[3];
    private boolean initialize = true;

    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.blob_detection_activity);
        currentColorView = (ImageView) findViewById(R.id.current_color);
        mOpenCvCameraView = (FancyCameraView) findViewById(R.id.blob_camera_preview);
        mOpenCvCameraView.setCvCameraViewListener(this);

        leftMenu.initializeLeftMenu(getResources(), getApplicationContext(), this);
        rightFragment = new ColorPickerFragment(R.id.drawer_layout_blob_detection_activity, this);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final ImageButton backButton = (ImageButton) findViewById(R.id.back);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mSensor = mSensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        backButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent changeActivity = new Intent(ColorBlobDetectionActivity.this, StartActivity.class);
                                                    ColorBlobDetectionActivity.this.startActivity(changeActivity);

                                                }
                                            }
        );

        final ImageButton takeAPhotoButton = (ImageButton) findViewById(R.id.blob_take_photo_button);
        //takeAPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(btnSize, btnSize));
        takeAPhotoButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                   /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                                                    String fileName = Environment.getExternalStorageDirectory().getPath() + "/image" + dateFormat.format(new Date()) + ".jpg";
                                                    mOpenCvCameraView.takePicture(fileName);
                                                    Toast.makeText(ColorBlobDetectionActivity.this, fileName + " saved", Toast.LENGTH_SHORT).show();
                                         */       }
                                            }
        );

        final ImageButton pickColorButton = (ImageButton) findViewById(R.id.pick_color);
        //pickColorButton.setLayoutParams(new LinearLayout.LayoutParams(btnSize, btnSize));
        pickColorButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent changeActivity = new Intent(ColorBlobDetectionActivity.this, MoldListActivity.class);
                                                   ColorBlobDetectionActivity.this.startActivity(changeActivity);

                                               }
                                            }
        );
    }

    private boolean isLandscape(int orientation){
        return (orientation >= (90 - THRESHOLD) && orientation <= (90 + THRESHOLD)) || (orientation >= (270 - THRESHOLD) && orientation <= (270 + THRESHOLD));
    }

    private boolean isPortrait(int orientation){
        return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD) || (orientation >= (180 - THRESHOLD) && orientation <= 180);
    }

    public void openColorPickerFragment(){
        if (COLOR_PICKER_ON == false) {
            if (getFragmentManager().findFragmentById(R.id.fragment_place) == null) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack("A")
                        .add(R.id.fragment_place, rightFragment)
                        .commit();
            }
            COLOR_PICKER_ON = true;
        }
    }

    public void closeColorPickerFragment(){
        rightFragment.closeDrawer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMenu.synchronizeLeftMenuState();
        rightFragment.synchronizeMenuState();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        COLOR_PICKER_ON = false;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
        rightFragment.changedMenuConfiguration(newConfig);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        //mSensorManager.registerListener(this, mSensor, 1000000);
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,255,0);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        closeColorPickerFragment();

        if (colorIsPicked()) {
            int cols = mRgba.cols();
            int rows = mRgba.rows();


            Display display = getWindowManager().getDefaultDisplay();
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            float viewX = event.getRawX() - location[0];
            float viewY = event.getRawY() - location[1];
            android.graphics.Point size = new android.graphics.Point();
            display.getSize(size);
            double width = size.x;
            double height = size.y;
            double y = rows - viewX*rows/width;
            double x = viewY*cols/height;
            Log.d(TAG, "POINT: "+x+ ", "+y);
            listOfPoints.add(new Point(x, y));
            Log.i(TAG, "Touch image coordinates: (" + (int)x + ", " + (int)y + ")");
            if (((int)x < 0) || ((int)y < 0) || ((int)x > cols) || ((int)y > rows)) return false;

            Rect touchedRect = getTouchedRect(cols, rows, (int) y, (int) x);

            Mat touchedRegionRgba = mRgba.submat(touchedRect);

            Mat touchedRegionHsv = new Mat();
            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            int pointCount = touchedRect.width * touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;

            mBlobColorRgba = convertScalarHsv2Rgba(mBlobColorHsv);

            Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                    ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

            mDetector.setHsvColor(mBlobColorHsv);

            mIsColorSelected = true;

            touchedRegionRgba.release();
            touchedRegionHsv.release();
        }

        return false; // don't need subsequent touch events
    }

   /* @Override
    public void onSensorChanged(SensorEvent event)
    {
        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                    event.values);
            SensorManager
                    .remapCoordinateSystem(mRotationMatrix,
                            SensorManager.AXIS_X, SensorManager.AXIS_Z,
                            mRotationMatrix);
            float[] orientationVals = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientationVals);
            if (initialize) {
                saveOrientationVals(orientationVals);
                initialize = false;
            }

            // Optionally convert the result from radians to degrees
            *//*orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);*//*

            *//*Log.d(TAG," Yaw: " + orientationVals[0] + "\n Pitch: "
                    + orientationVals[1] + "\n Roll (not used): "
                    + orientationVals[2]);*//*

            float d = inMemoryOrientationVals[0] - orientationVals[0];
            if (listOfPoints.size()>0 && (Math.abs(d)>0.2)) {
                Point point = listOfPoints.get(0);
                Log.d(TAG, "Old point: " + point.x + ", " + point.y);
                listOfPoints.clear();

                double cos = Math.cos(d/2);
                Log.d(TAG, "d: "+d+", cos(d/2): "+cos);
                double v = point.y / cos;
                Point newPoint = new Point(point.x, v);
                listOfPoints.add(newPoint);
                Log.d(TAG, "New point: " + newPoint.x + ", " + newPoint.y);
                added = true;
                Imgproc.circle(mRgba, newPoint, 10, new Scalar(205, 201, 201, 100), -1);
                saveOrientationVals(orientationVals);
            }
        }
    }*/

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float d = mOrientation[0] - inMemoryOrientationVals[0];
            if (listOfPoints.size()>0 && (Math.abs(d)>0.2)) {
                Point point = listOfPoints.get(0);
                double cos = Math.cos(d);
                Log.d(TAG, "d: " + d + ", cos(d): " + cos);
                double v = point.y / cos;
                Point newPoint = new Point(point.x, v);
                listOfPoints.remove(0);
                listOfPoints.add(newPoint);
                Log.d(TAG, "New point: " + newPoint.x + ", " + newPoint.y);
                added = true;
                Imgproc.circle(mRgba, newPoint, 10, new Scalar(205, 201, 201, 100), -1);
            }
            Log.i("OrientationTestActivity", String.format("Orientation: %f, %f, %f",
                    Math.toDegrees(mOrientation[0]), Math.toDegrees(mOrientation[1]), Math.toDegrees(mOrientation[2])));
            if (initialize) {
                inMemoryOrientationVals[0] = mOrientation[0];
                inMemoryOrientationVals[1] = mOrientation[1];
                inMemoryOrientationVals[2] = mOrientation[2];
                initialize = false;
            }
        }
    }

    private void saveOrientationVals(float[] orientationVals) {
        inMemoryOrientationVals[0] = orientationVals[0];
        inMemoryOrientationVals[1] = orientationVals[1];
        inMemoryOrientationVals[2] = orientationVals[2];
    }

    @NonNull
    private Rect getTouchedRect(int cols, int rows, int y, int x) {
        Rect touchedRect = new Rect();

        touchedRect.x = (x > 4) ? x - 4 : 0;
        touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.y = (y > 4) ? y - 4 : 0;
        touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;
        return touchedRect;
    }

    private boolean colorIsPicked() {
        return ColorPickerFragment.getLastPicked()!=null;
    }

    public void closeRightPaneIfItIsOpen() {

        closeColorPickerFragment();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        Mat orig = mRgba;
        if (mIsColorSelected && colorIsPicked()) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            orig = mRgba.clone();

          //  if (pickedColor!=null && !pickedColor.equals(ColorPickerFragment.getLastPicked())) Log.d(TAG,"picked color: "+ (int)pickedColor.val[0] + ", " + (int)pickedColor.val[1] + ", " + (int)pickedColor.val[2]);
            pickedColor = ColorPickerFragment.getLastPicked();

            Imgproc.drawContours(orig, contours, -1, pickedColor, -1);
            Core.addWeighted(orig, 0.4, mRgba, 0.6, 0.0, mRgba);
           /* Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(pickedColor);*/
        }
        if (!listOfPoints.isEmpty()) {
            for (Point p : listOfPoints) {
                //Imgproc.circle(mRgba, p, 10, new Scalar(205, 201, 201, 100), -1);
            }

        }

        return mRgba;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}