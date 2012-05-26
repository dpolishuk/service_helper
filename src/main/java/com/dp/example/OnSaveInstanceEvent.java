package com.dp.example;

import android.os.Bundle;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 25.05.12
 * Time: 21:35
 */
public class OnSaveInstanceEvent {
    Bundle bundle;

    public OnSaveInstanceEvent(Bundle savedInstanceState) {
        this.bundle = savedInstanceState;
    }

    public Bundle getSavedInstance() {
        return bundle;
    }
}
