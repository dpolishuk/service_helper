package com.dp.example;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.inject.InjectView;

public class MyActivity extends MyRoboActivity {
    @Inject
    ServiceHelper serviceHelper;

    @InjectView(R.id.counter)
    TextView counterView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (null == savedInstanceState) {
            serviceHelper.startService(new Intent(this, ApiService.class), new RequestHandler() {
                @Override
                public void onRequestRunning(Bundle b) {
                    counterView.setText(b.getString("counter"));
                }
            });
        }
    }
}
