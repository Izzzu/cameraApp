package com.kulak.izabel.cameraapp.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.kulak.izabel.cameraapp.FloodFill;

import org.junit.Ignore;
import org.junit.Test;


public class FloodFillTest {

    private FloodFill floodFill = new FloodFill();

    @Test
    @Ignore
    public void shouldSTH() {
        Bitmap bitmap = BitmapFactory.decodeFile("img.png");
        /*bitmap.setWidth(100);
        bitmap.setHeight(200);
        bitmap.setConfig(Bitmap.Config.ARGB_4444);*/
        Bitmap floodFilledBitmap = this.floodFill.floodFill(bitmap, new Point(0, 0), 3, 8);
        System.out.println(bitmap);
        System.out.println(floodFilledBitmap);

    }



}