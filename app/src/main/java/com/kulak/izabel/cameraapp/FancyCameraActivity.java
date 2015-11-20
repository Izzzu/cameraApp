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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.cvtColor;

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
    private Mat mRgba = new Mat();


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
        TOUCHED = false;
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
            Log.d(TAG, "before prepare mat");
            mRgba = inputFrame.rgba();
            return prepareMat(mRgba);
        }
        Log.d(TAG, "after if mat");

        return inputFrame.rgba();

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (fancyCameraView.getWidth() - cols) / 2;
        int yOffset = (fancyCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;
        TOUCHED = true;
        touchPoint = new Point(x, y);
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

        //******************************


       /* Mat hsvImage = src.clone();
        Mat gray = src.clone();
        Size size = src.size();
        Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);

        opencv_core.IplImage hChannel = cvCreateImage(cvSize, image.depth(), 1);
        opencv_core.IplImage sChannel = cvCreateImage(cvSize, image.depth(), 1);
        opencv_core.IplImage vChannel = cvCreateImage(cvSize, image.depth(), 1);
        cvSplit(hsvImage, hChannel, sChannel, vChannel, null);

        opencv_core.IplImage cvInRange = cvCreateImage(cvSize, image.depth(), 1);
        opencv_core.CvScalar source=new opencv_core.CvScalar(72/2,0.07*255,66,0); //source color to replace
        Scalar sourceScalar = new Scalar(72/2,0.07*255,66,0);
        opencv_core.CvScalar from=getScaler(source,false);
        opencv_core.CvScalar to=getScaler(source, true);

        cvInRangeS(hsvImage, from , to, cvInRange);

        opencv_core.IplImage dest = cvCreateImage(cvSize, image.depth(), image.nChannels());

        opencv_core.IplImage temp = cvCreateImage(cvSize, IPL_DEPTH_8U, 2);
        cvMerge(hChannel, sChannel, null, null, temp);

        cvSet(temp, new opencv_core.CvScalar(45, 255, 0, 0), cvInRange);// destination hue and sat
        cvSplit(temp, hChannel, sChannel, null, null);
        cvMerge(hChannel, sChannel, vChannel, null, dest);
        cvCvtColor(dest, dest, CV_HSV2BGR);*/

        //*********************************************

        // init

        Mat gray =src.clone();
        Mat end = src.clone();
        Mat hsvImage = src.clone();
        Mat laplacian = src.clone();
        cvtColor(src, hsvImage, Imgproc.COLOR_BGRA2BGR);
        cvtColor(src, laplacian, Imgproc.COLOR_BGRA2BGR);
        cvtColor(src, end, Imgproc.COLOR_BGRA2BGR);
        Mat markers = hsvImage.clone();

        cvtColor(hsvImage, gray, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.cvtColor(hsvImage, markers, Imgproc.COLOR_BGR2GRAY);
        //gray.convertTo(markers, CvType.CV_32SC1);
        Log.d(TAG, "markers typeee:" + markers.type());
        Log.d(TAG, "hsvimg type:" + hsvImage.type());
        Log.d(TAG, "src type:" + src.type());
        Log.d(TAG, "CvType.CV_8UC3 :" + CvType.CV_8UC3);
        Log.d(TAG, "CvType.CV_32SC1 :" + CvType.CV_32SC1);

        org.opencv.core.Point seedPoint = new org.opencv.core.Point((touchPoint.x * 640) / fancyCameraView.getWidth(), (touchPoint.y*480)/fancyCameraView.getHeight());
        //src.convertTo(end, 32);
        //Imgproc.cvtColor(end, end, CvType.CV_32S);
        //Imgproc.Canny(gray, gray, 50, 200);
       // List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
       // Mat hierarchy = new Mat();
// find contours:
        Log.d(TAG, "A");

        //markers = Mat.zeros(hsvImage.rows(), hsvImage.cols(), CvType.CV_8UC1);
        //circle(markers, seedPoint, 10, new Scalar(255, 255, 255));


        Mat gray8 = markers.clone();
        //gray8 = new Mat(markers.size(), CvType.CV_8UC1);
        Log.d(TAG, "hsvimg channels:" + hsvImage.channels());
        Log.d(TAG, "Gray8 channels:" + gray8.channels());
        Log.d(TAG, "Markers channels:" + markers.channels());

        //Imgproc.cvtColor(markers, gray8, Imgproc.COLOR_GRAY2BGR);
        cvtColor(markers, gray8, Imgproc.COLOR_BGR2GRAY);
        Scalar mean = Core.mean(gray8);
        Log.d(TAG, "mean: " + mean);
        //Imgproc.adaptiveThreshold(gray8, gray8, 50, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 1);
        //Imgproc.threshold(gray8, gray8, mean.val[0], 255, Imgproc.THRESH_BINARY);
    /*Imgproc.erode(gray8, gray8, new Mat(), new Point(-1, -1), 2);*/

        Canny(gray8, gray8, 50, 150);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfInt4 hierarchy = new MatOfInt4();
       // gray8 = Mat.zeros(gray8.rows() + 2, gray8.cols() + 2, CvType.CV_8U);
        //circle(gray8, seedPoint, 10, new Scalar(255, 255, 255), -1, LINE_4, 0);
        Imgproc.findContours(gray8, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        //Toast.makeText(getApplicationContext(), contours.size()+" yo", Toast.LENGTH_SHORT).show();
        //Imgproc.drawContours(hsvImage, contours, 0, new Scalar(0, 255, 255), -1, 1, hierarchy, 5, new org.opencv.core.Point(5,5));

        Log.d(TAG, "contours size: "+ contours.size());
       for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(hsvImage, contours, contourIdx, new Scalar(0, 255/(10+contourIdx), 255), 1, 1, hierarchy, 5, new org.opencv.core.Point(0.1,0.1));
        }
        gray8.convertTo(gray8, CvType.CV_32S);
        //Imgproc.watershed(hsvImage, gray8);
        gray8.convertTo(gray8, CvType.CV_8UC1);

        Scalar lowerDifference = new Scalar(2, 2, 2);
        Scalar upperDifference = new Scalar(2, 2, 2);
        Scalar newVal = new Scalar(255, 255, 255);

        Mat mask = Mat.zeros(gray8.rows() + 2, gray8.cols() + 2, CvType.CV_8U);

      /*  Imgproc.floodFill(gray8, mask, seedPoint, newVal, new Rect(), lowerDifference,
                upperDifference, 8 | Imgproc.FLOODFILL_FIXED_RANGE); //flag 4 or 8*//*GaussianBlur
//    */   // Imgproc.GaussianBlur(hsvImage, hsvImage, new Size(11,11), 6, 6, 0);
        // Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Log.d(TAG, "B");

        //for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
          //  Imgproc.drawContours(laplacian, contours, contourIdx, new Scalar(0, 0, 255), -1);
        //}
        Imgproc.floodFill(hsvImage, mask, seedPoint, newVal, new Rect(), lowerDifference,
                upperDifference, 8 | Imgproc.FLOODFILL_FIXED_RANGE); //flag 4 or 8*/
       // Imgproc.watershed(hsvImage, markers);
        Log.d(TAG, "Laplacian type:" + laplacian.type());
        Log.d(TAG, "hsvImage type:" + hsvImage.type());
        Log.d(TAG, "end type:" + end.type());
        Log.d(TAG, "Laplacian channels:" + laplacian.channels());
        Log.d(TAG, "hsvImage channels:" + hsvImage.channels());
        Log.d(TAG, "end channels:" + end.channels());
       // Imgproc.Laplacian(laplacian, laplacian, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT);

        //Core.addWeighted(hsvImage, 1.5, laplacian, -0.5, 0, end);

        //Core.addWeighted(src, 3, hsvImage, 3, 3, end);
        //Core.add(end, hsvImage, end);

       //Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_HSV2BGR);
       return hsvImage;
    }



    private int thresold = 2;
    Scalar getScaler(Scalar seed,boolean plus){
        if(plus){
            return new Scalar(seed.val[0]+(seed.val[0]*thresold),seed.val[1]+(seed.val[1]*thresold),seed.val[2]+(seed.val[2]*thresold));
        }else{
            return new Scalar(seed.val[0]-(seed.val[0]*thresold),seed.val[1]-(seed.val[1]*thresold),seed.val[2]-(seed.val[2]*thresold));
        }
    }

}
