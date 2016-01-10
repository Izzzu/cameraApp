package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class PhotoActivity extends Activity implements View.OnTouchListener, ColorPickerOwner {

    private static final int SELECT_PICTURE = 1;
    private final LeftMenu leftMenu = new LeftMenu(R.id.drawer_layout_photo_activity);

    private ImageView imageView;
    private String TAG = "PhotoActivity";


    private CharSequence appTitle = "";
    private static boolean COLOR_PICKER_ON;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {

                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    private ColorPickerFragment rightFragment;
    private Scalar pickedColor = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
        rightFragment.onConfigurationChanged(newConfig);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        setContentView(R.layout.activity_photo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageView = (ImageView) findViewById(R.id.selected_photo);
        imageView.setOnTouchListener(PhotoActivity.this);
        leftMenu.initializeLeftMenu(getResources(), getApplicationContext(), this);
        rightFragment = new ColorPickerFragment(R.id.drawer_layout_photo_activity, this);


        final ImageButton backButton = (ImageButton) findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent changeActivity = new Intent(PhotoActivity.this, StartActivity.class);
                                              PhotoActivity.this.startActivity(changeActivity);
                                          }
                                      }
        );

        final ImageButton pickAPhotoButton = (ImageButton) findViewById(R.id.pick_photo_button);
        //pickAPhotoButton.setLayoutParams(new LinearLayout.LayoutParams(btnSize, btnSize));
        pickAPhotoButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    startActivityForResult(intent, SELECT_PICTURE);

                                                }
                                            }
        );

        final ImageButton pickColorButton = (ImageButton) findViewById(R.id.pick_color);
        //pickColorButton.setLayoutParams(new LinearLayout.LayoutParams(btnSize, btnSize));
        pickColorButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   // if (savedInstanceState == null) {

                                                   // }

                                               }
                                           }
        );

        mRgba = new Mat(imageView.getHeight(), imageView.getWidth(), CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255, 255, 0);
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
    public void setTitle(CharSequence title) {
        appTitle = title;
        getActionBar().setTitle(appTitle);
    }

    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        closeColorPickerFragment();

        if (colorIsPicked() && mIsColorSelected) {
            Log.d(TAG, "Color is picked");
            Drawable imgDrawable = ((ImageView)imageView).getDrawable();
            bitmap = ((BitmapDrawable)imgDrawable).getBitmap();
            Utils.bitmapToMat(bitmap,mRgba);

            int cols = mRgba.cols();
            int rows = mRgba.rows();

            int xOffset = (imageView.getWidth() - cols) / 2;
            int yOffset = (imageView.getHeight() - rows) / 2;

            int x = (int) event.getX() - xOffset;
            int y = (int) event.getY() - yOffset;

            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

            if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

            org.opencv.core.Rect touchedRect = new org.opencv.core.Rect();

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

            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Mat orig = mRgba.clone();

            if (pickedColor != null && !pickedColor.equals(ColorPickerFragment.getLastPicked()))
                Log.d(TAG, "picked color: " + (int) pickedColor.val[0] + ", " + (int) pickedColor.val[1] + ", " + (int) pickedColor.val[2]);
            pickedColor = ColorPickerFragment.getLastPicked();

            Imgproc.drawContours(orig, contours, -1, pickedColor, -1);
            Core.addWeighted(orig, 0.4, mRgba, 0.6, 0.0, mRgba);
            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(pickedColor);

            Imgproc.circle(mRgba, new Point(x, y), 3, new Scalar(255, 255, 255), -1);
            Log.d(TAG, "Mat to bitmap");
            Utils.matToBitmap(mRgba, bitmap);
            Log.d(TAG, "Set image bitmap");
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "Invalidate");
            imageView.invalidate();
            Log.d(TAG, "Invalidating done");
        }
        return false; // don't need subsequent touch events
    }

    private boolean colorIsPicked() {
        return ColorPickerFragment.getLastPicked() != null;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    //UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            try {
                pickAPhoto(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null)
            bitmap.recycle();
    }

    private void pickAPhoto(Intent data) throws FileNotFoundException {
        Uri selectedImage = data.getData();
        mCurrentPhotoPath = selectedImage.getPath();
        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
        //imageView.setImageURI(selectedImage);
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        Log.d(TAG, "targetW " + targetW);
        Log.d(TAG, "targetH " + targetH);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Log.d(TAG, "A ");
        bitmap = BitmapFactory.decodeStream(imageStream, new Rect(), bmOptions);

        //BitmapFactory.decodeStream(imageStream, new Rect(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.d(TAG, "photoW " + photoW);
        Log.d(TAG, "photoH " + photoH);
        int scaleFactor = 1;
        if (photoH > targetH) {
            scaleFactor = Math.round((float) photoH / (float) targetH);
        }
        int expectedWidth = photoW / scaleFactor;
        if (expectedWidth > targetW) {
            scaleFactor = Math.round((float) photoW / (float) targetW);
        }
        // Determine how much to scale down the image
        //scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        Log.d(TAG, "scaleFactor-" + scaleFactor);
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 1;
        bmOptions.inPurgeable = true;
        Log.d(TAG, "B ");
        InputStream imageStream2 = getContentResolver().openInputStream(selectedImage);

        bitmap = BitmapFactory.decodeStream(imageStream2, new Rect(), bmOptions);

        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMenu.synchronizeLeftMenuState();
        rightFragment.synchronizeMenuState();
    }

    private void closeRightPaneIfItIsOpen() {
        closeColorPickerFragment();
    }

    public void openColorPickerFragment() {
        if (COLOR_PICKER_ON != true) {
            getFragmentManager()
                    .beginTransaction()
                    .addToBackStack("A")
                    .add(R.id.fragment_place, rightFragment)
                    .commit();
            COLOR_PICKER_ON = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        COLOR_PICKER_ON = false;

    }

    @Override
    public void closeColorPickerFragment() {
        rightFragment.closeDrawer();

    }

}
