package com.dp.example;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import roboguice.util.Ln;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 05.02.12
 * Time: 17:05
 */
public class ApiService extends IntentService {
    public static final int STATUS_RUNNING = 1;

    public ApiService() {
        super(ApiService.class.getSimpleName());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApiResultReceiver receiver = intent.getParcelableExtra(Constants.EXTRA_STATUS_RECEIVER);

        Bundle b = new Bundle();

        int i = 0;
        b.putString("counter", String.valueOf(i++));

        try {
            while (true) {
                b.putString("counter", String.valueOf(i++));
                receiver.send(ApiService.STATUS_RUNNING, b);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Ln.e(e);
        }
    }
}
