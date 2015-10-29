package com.kulak.izabel.cameraapp;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;


public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testApp() {
        // Testcase
    }

    Intent mainActivityIntent;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivityIntent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
    }

    public void cameraActivityShouldBeLaunched() {
        startActivity(mainActivityIntent, null, null);
        final Button launchCameraButton = (Button) getActivity().findViewById(R.id.button_camera);

        launchCameraButton.performClick();

        final Intent launchIntent = getStartedActivityIntent();

        assertNotNull(launchIntent);
        assertTrue(isFinishCalled());

    }

}
