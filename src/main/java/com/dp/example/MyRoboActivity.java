package com.dp.example;

import android.os.Bundle;
import roboguice.activity.RoboActivity;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 25.05.12
 * Time: 21:33
 */
public class MyRoboActivity extends RoboActivity {
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        eventManager.fire(new OnSaveInstanceEvent(outState));
        super.onSaveInstanceState(outState);
    }
}
