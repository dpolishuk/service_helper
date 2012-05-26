package com.dp.example;

import android.os.*;
import roboguice.util.Ln;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 25.05.12
 * Time: 20:49
 */
public class ApiResultReceiver implements Parcelable {
    final boolean local;
    final Handler handler;
    int ticket;

    IApiResultReceiver receiver;

    class MyRunnable implements Runnable {
        final int resultCode;
        final Bundle resultData;

        MyRunnable(int resultCode, Bundle resultData) {
            this.resultCode = resultCode;
            this.resultData = resultData;
        }

        public void run() {
            onReceiveResult(resultCode, resultData, ticket);
        }
    }

    class MyResultReceiver extends IApiResultReceiver.Stub {
        public void send(int rc, Bundle b) {
            if (handler != null) {
                handler.post(new MyRunnable(rc, b));
            } else {
                onReceiveResult(rc, b, ticket);
            }
        }
    }

    public ApiResultReceiver(Handler handler, int ticket) {
        local = true;
        this.handler = handler;
        this.ticket = ticket;
    }

    public void send(int rc, Bundle b) {
        if (local) {
            if (handler != null) {
                handler.post(new MyRunnable(rc, b));
            } else {
                onReceiveResult(rc, b, ticket);
            }
            return;
        }

        if (receiver != null) {
            try {
                receiver.send(rc, b);
            } catch (RemoteException e) {
                Ln.e(e);
            }
        }
    }

    protected void onReceiveResult(int resultCode, Bundle resultData, int ticket) {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        synchronized (this) {
            if (receiver == null) {
                receiver = new MyResultReceiver();
            }

            out.writeInt(ticket);
            out.writeStrongBinder(receiver.asBinder());
        }
    }

    ApiResultReceiver(Parcel in) {
        local = false;
        handler = null;
        ticket = in.readInt();
        receiver = IApiResultReceiver.Stub.asInterface(in.readStrongBinder());
    }

    public static final Parcelable.Creator<ApiResultReceiver> CREATOR = new Parcelable.Creator<ApiResultReceiver>() {
        public ApiResultReceiver createFromParcel(Parcel in) {
            return new ApiResultReceiver(in);
        }

        public ApiResultReceiver[] newArray(int size) {
            return new ApiResultReceiver[size];
        }
    };
}