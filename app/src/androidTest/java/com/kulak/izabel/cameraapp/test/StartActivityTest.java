package com.kulak.izabel.cameraapp.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ImageButton;

import com.kulak.izabel.cameraapp.R;
import com.kulak.izabel.cameraapp.StartActivity;


public class StartActivityTest extends ActivityUnitTestCase<StartActivity> {


    private Intent mLaunchIntent;

    public StartActivityTest() {
        super(StartActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), StartActivity.class);

    }

    @MediumTest
    public void testNextActivityWasLaunchedWithIntent() {
        startActivity(mLaunchIntent, null, null);
        final ImageButton launchNextButton =
                (ImageButton) getActivity()
                        .findViewById(R.id.button_run_blob_detection_camera);
        launchNextButton.performClick();

        final Intent launchIntent = getStartedActivityIntent();
        assertNotNull("Intent was null", launchIntent);
        String className = launchIntent.getComponent().getClassName();
        assertEquals(className, "com.kulak.izabel.cameraapp.CameraActivity");
    }
}