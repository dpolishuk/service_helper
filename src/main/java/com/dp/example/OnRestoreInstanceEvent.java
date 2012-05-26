package com.dp.example;

import android.os.Bundle;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 26.05.12
 * Time: 13:30
 */
public class OnRestoreInstanceEvent {
    Bundle bundle;

    public OnRestoreInstanceEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getRestoredInstance() {
        return bundle;
    }
}
