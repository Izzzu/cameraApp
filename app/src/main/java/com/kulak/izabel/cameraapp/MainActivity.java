package com.kulak.izabel.cameraapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static String logPrefix = "CameraApp";
    private static int TAKE_PICTURE = 1;
    private static int RECORD_VIDEO = 2;
    private Uri imageUri;
    private Uri videoUri;
    private View.OnClickListener cameraPictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takeAPhoto(v);
        }
    };
    private View.OnClickListener cameraVideoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takeAVideo(v);
        }
    };

    private void takeAVideo(View v) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "video_" + videoNr + ".mp4");
        videoNr++;
        videoUri = Uri.fromFile(video);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RECORD_VIDEO);
        }
    }

    private static int picNr = 0;
    private static int videoNr = 0;

    private void takeAPhoto(View v) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture_" + picNr + ".jpg");
        picNr++;
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageUri;
                getContentResolver().notifyChange(selectedImage, null); //TODO: check wtf is that

                ImageView imageView = (ImageView) findViewById(R.id.image_camera);
                imageView.setVisibility(View.VISIBLE);
                ContentResolver contentResolver = getContentResolver();
                Bitmap bitmap;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.e(logPrefix, e.toString());
                    e.printStackTrace();

                }
            }
        } else if (requestCode == RECORD_VIDEO) {
            VideoView videoView = (VideoView) findViewById(R.id.video_camera);
            if (resultCode == RESULT_OK) {
                videoView.setVisibility(View.VISIBLE);
                Uri videoContentUri = intent.getData();
                videoView.setVideoURI(videoContentUri);
                videoView.start();
                Toast.makeText(MainActivity.this, videoContentUri.toString(), Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(cameraPictureListener);

        Button videoButton = (Button) findViewById(R.id.button_video);
        videoButton.setOnClickListener(cameraVideoListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
