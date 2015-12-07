package com.kulak.izabel.cameraapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        leftMenu.changedLeftMenuConfiguration(newConfig);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageView = (ImageView) findViewById(R.id.selected_photo);

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
                                                      startActivityForResult(intent,SELECT_PICTURE);

                                                  }
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



    @Override
    public void setTitle(CharSequence title) {
        appTitle = title;
        getActionBar().setTitle(appTitle);
    }



    //UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==SELECT_PICTURE && resultCode==RESULT_OK && null !=data)
        {

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
        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
        Bitmap selectFile = BitmapFactory.decodeStream(imageStream);
        Drawable d = new BitmapDrawable(selectFile);
       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setImageURI(selectedImage);
       /* }
        else {
            imageView.setImageDrawable(d);
        }*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMenu.synchronizeLeftMenuState();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
