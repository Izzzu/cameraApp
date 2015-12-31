package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoActivity extends Activity implements View.OnTouchListener {

    private static final int SELECT_PICTURE = 1;
    private final LeftMenu leftMenu = new LeftMenu(R.id.drawer_layout_photo_activity);

    private ImageView imageView;
    private String TAG = "PhotoActivity";


    private CharSequence appTitle = "";
    private static boolean COLOR_PICKER_ON;
    private String mCurrentPhotoPath;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageView = (ImageView) findViewById(R.id.selected_photo);
        imageView.setOnTouchListener(PhotoActivity.this);
        leftMenu.initializeLeftMenu(getResources(), getApplicationContext(), this);

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
                                                   getFragmentManager()
                                                           .beginTransaction()
                                                           .addToBackStack("A")
                                                           .add(R.id.fragment_place, new ColorPickerActivity())
                                                           .commit();
                                                   COLOR_PICKER_ON = true;
                                                   // }

                                               }
                                           }
        );
    }


    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getActionBar().setTitle(appTitle);
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

            // imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }
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
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream, new Rect(), bmOptions);


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
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "On touch!!!");
        closeRightPaneIfItIsOpen();
        return false;
    }

    private void closeRightPaneIfItIsOpen() {
        if (COLOR_PICKER_ON) {
            getFragmentManager().popBackStack();
            COLOR_PICKER_ON = false;
            Log.d(TAG, "On touch in IF!!!");
        }
    }

}
