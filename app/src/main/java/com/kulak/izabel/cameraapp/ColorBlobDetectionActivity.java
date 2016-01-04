package com.kulak.izabel.cameraapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

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

import java.util.List;

public class ColorBlobDetectionActivity extends FragmentActivity implements View.OnTouchListener, CvCameraViewListener2, ColorPickerOwner {
    private static final String  TAG              = "OCVSample::Activity";

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

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.blob_detection_activity);
        currentColorView = (ImageView) findViewById(R.id.current_color);
        mOpenCvCameraView = (FancyCameraView) findViewById(R.id.blob_camera_preview);
        mOpenCvCameraView.setCvCameraViewListener(this);
        leftMenu.initializeLeftMenu(getResources(), getApplicationContext(), this);
        rightFragment = new ColorPickerFragment(R.id.drawer_layout_blob_detection_activity, this);

        final ImageButton backButton = (ImageButton) findViewById(R.id.back);

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


                                               }
                                            }
        );
    }

    public void openColorPickerFragment(){
        if (COLOR_PICKER_ON != true) {
            getFragmentManager()
                    .beginTransaction()
                    .addToBackStack("A")
                    .add(R.id.fragment_place, rightFragment)
                    .commit();
            COLOR_PICKER_ON = true;
        }

    }

    public void closeColorPickerFragment(){
        rightFragment.closeDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
        rightFragment.changedMenuConfiguration(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMenu.synchronizeLeftMenuState();
        rightFragment.synchronizeMenuState();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
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





    public boolean onTouch(View v, MotionEvent event) {
        closeRightPaneIfItIsOpen();

        if (colorIsPicked()) {
            int cols = mRgba.cols();
            int rows = mRgba.rows();

            int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
            int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

            int x = (int) event.getX() - xOffset;
            int y = (int) event.getY() - yOffset;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

            if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

            Rect touchedRect = new Rect();

            touchedRect.x = (x > 4) ? x - 4 : 0;
            touchedRect.y = (y > 4) ? y - 4 : 0;

            touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

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

            Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

            mIsColorSelected = true;

            touchedRegionRgba.release();
            touchedRegionHsv.release();

            Imgproc.circle(mRgba, new Point(x, y), 3, new Scalar(255, 255, 255), -1);

        }
        return false; // don't need subsequent touch events
    }

    private boolean colorIsPicked() {
        return ColorPickerFragment.getLastPicked()!=null;
    }

    public void closeRightPaneIfItIsOpen() {
        /*if (COLOR_PICKER_ON) {
            getFragmentManager().popBackStack();
            Log.d(TAG, "On touch");
            COLOR_PICKER_ON = false;
        }*/
        rightFragment.closeDrawer();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mIsColorSelected && colorIsPicked()) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Mat orig = mRgba.clone();

            //if(pickedColor!=null && !pickedColor.equals(ColorPickerFragment.getLastPicked()))
           // Log.d(TAG, "last picked color: "+ pickedColor.val[0] + "," + pickedColor.val[1] + "," + pickedColor.val[2]);
            //{

            if (pickedColor!=null && !pickedColor.equals(ColorPickerFragment.getLastPicked())) Log.d(TAG,"picked color: "+ (int)pickedColor.val[0] + ", " + (int)pickedColor.val[1] + ", " + (int)pickedColor.val[2]);
            pickedColor = ColorPickerFragment.getLastPicked();
            //currentColorView.setBackgroundColor(Color.rgb((int) pickedColor.val[0], (int) pickedColor.val[1], (int) pickedColor.val[2]));

           // }
         //   else {

        //        pickedColor = ColorPickerFragment.getLastPicked();
                //currentColorView.setBackgroundColor(Color.rgb((int) pickedColor.val[0], (int) pickedColor.val[1], (int) pickedColor.val[2]));

        //    }

            Imgproc.drawContours(orig, contours, -1, pickedColor, -1);
            Core.addWeighted(orig, 0.4, mRgba, 0.6, 0.0, mRgba);
            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(pickedColor);

           // Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            //mSpectrum.copyTo(spectrumLabel);
        }


        return mRgba;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}