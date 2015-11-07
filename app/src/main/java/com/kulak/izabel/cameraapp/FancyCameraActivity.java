package com.kulak.izabel.cameraapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FancyCameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private static final String TAG = "FancyCameraActivity";

    private static boolean TOUCHED = false;

    private FancyCameraView fancyCameraView;
    private Mat inputFrameMat;
    private Point touchPoint;
    private Random random = new Random();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    fancyCameraView.enableView();
                    fancyCameraView.setOnTouchListener(FancyCameraActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.fancy_camera_activity);

        fancyCameraView = (FancyCameraView) findViewById(R.id.fancy_camera_preview);
        fancyCameraView.setVisibility(SurfaceView.VISIBLE);
        fancyCameraView.setCvCameraViewListener(this);

        final Button takeAPhotoButton = (Button) findViewById(R.id.take_photo_button);
        takeAPhotoButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                                                    String fileName = Environment.getExternalStorageDirectory().getPath() + "/image" + dateFormat.format(new Date()) + ".jpg";
                                                    fancyCameraView.takePicture(fileName);
                                                    Toast.makeText(FancyCameraActivity.this, fileName + " saved", Toast.LENGTH_SHORT).show();

                                                }
                                            }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fancyCameraView != null) {
            fancyCameraView.disableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fancyCameraView != null) {
            fancyCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (TOUCHED) {
            return prepareMat(inputFrame.rgba());
        }

        return inputFrame.rgba();

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        TOUCHED = true;
        touchPoint = new Point((int)event.getX(), (int)event.getY());
        Log.d(TAG, "onTouch event");


        return false;
    }

    private Mat prepareMat(Mat src) {

        Log.d(TAG, "src type:" + src.type());
/*        Mat gray = src.clone();
        Mat hsvImage = src.clone();
        Mat destMat = new Mat();

      //  Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_BGR2HSV);
       // Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.GaussianBlur(src, src, new Size(5,5), 2.2, 2);

        /// Apply Laplace function
        *//*
        Laplacian( gray, dst, CV_16S, 3, 1, 0, BORDER_DEFAULT );
        convertScaleAbs( dst, abs_dst );
        imshow( "result", abs_dst );*//*
        //Imgproc.Laplacian(gray, gray,  CvType.CV_8U);
        Mat kernel = new Mat(9,9, CvType.CV_32F){
            {
                put(0,0,0);
                put(0,1,-1);
                put(0,2,0);

                put(1,0-1);
                put(1,1,4);
                put(1,2,-1);

                put(2,0,0);
                put(2,1,-1);
                put(2,2,0);
            }
        };
        int b = random.nextInt(256);
        int g = random.nextInt(256);
        int r = random.nextInt(256);
        Rect rect = new Rect();

        int newMaskVal = 255;
        int lowerDiff = 2;
        int upperDiff = 2;

        Scalar lowerDifference = new Scalar(lowerDiff,lowerDiff,lowerDiff);
        Scalar upperDifference = new Scalar(upperDiff,upperDiff,upperDiff);

        Scalar newVal = true ? new Scalar(b, g, r) : new Scalar(r*0.299 + g*0.587 + b*0.114);

        int flags = 4 + (newMaskVal << 8) +
                ((Imgproc.FLOODFILL_FIXED_RANGE));//Imgproc.FLOODFILL_MASK_ONLY);
        int area = 0;
        Log.d(TAG, "channels: "+ src.channels());
        int number = ~(Imgproc.FLOODFILL_MASK_ONLY);

        org.opencv.core.Point seedPoint = new org.opencv.core.Point((touchPoint.x * 640) / fancyCameraView.getWidth(), (touchPoint.y*480)/fancyCameraView.getHeight());
        Log.d(TAG, "touch point:"+ touchPoint.toString());
        Log.d(TAG, "opencv point:"+ seedPoint.toString());
        Log.d(TAG, "image points: "+ hsvImage.rows() + " - " + hsvImage.cols());


        Mat mask = Mat.zeros(hsvImage.rows() + 2, hsvImage.cols() + 2, CvType.CV_8U);
        Mat black = new Mat(hsvImage.rows() + 2, hsvImage.cols() + 2, CvType.CV_8U);
       // Mat maskROI = new Mat(new Rect(seedPoint, hsvImage.size()));
        new Mat();
        *//*Imgproc.floodFill(hsvImage, mask.inv(), seedPoint, newVal, rect, lowerDifference,
                upperDifference, 4 + (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);
 *//*
        double[] doubles = hsvImage.get((int) seedPoint.x, (int) seedPoint.y);

        for (double d : doubles) {

            Log.d(TAG, "double:: "+d);
        }
        Imgproc.floodFill(hsvImage, mask, seedPoint, newVal, rect, lowerDifference,
                upperDifference, 4 + (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);*/


        //Imgproc.filter2D(src, src, -1, kernel);
        return src;
        /*Imgproc.Canny(gray, gray, 50, 200);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
// find contours:
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(src, contours, contourIdx, new Scalar(0, 0, 255), -1);
        }
        return src;
    */
    }




}
