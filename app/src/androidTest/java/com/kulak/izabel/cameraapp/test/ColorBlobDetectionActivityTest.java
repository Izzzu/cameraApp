package com.kulak.izabel.cameraapp.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

import com.kulak.izabel.cameraapp.ColorBlobDetectionActivity;
import com.kulak.izabel.cameraapp.R;


public class ColorBlobDetectionActivityTest extends ActivityUnitTestCase<ColorBlobDetectionActivity> {


    private Intent mLaunchIntent;

    public ColorBlobDetectionActivityTest() {
        super(ColorBlobDetectionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), ColorBlobDetectionActivity.class);
    }


    @MediumTest
    public void shouldLaunchMoldListActivity() {

        startActivity(mLaunchIntent, null, null);
        final Button launchNextButton =
                (Button) getActivity()
                        .findViewById(R.id.color_pick_button);
        launchNextButton.performClick();

        final Intent launchIntent = getStartedActivityIntent();
        assertNotNull("Intent was null", launchIntent);
        String className = launchIntent.getComponent().getClassName();
        assertEquals(className, "com.kulak.izabel.cameraapp.activity.MoldListActivity");
    }
}