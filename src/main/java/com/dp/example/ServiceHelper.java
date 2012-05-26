package com.dp.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.inject.Inject;
import roboguice.event.Observes;
import roboguice.util.Ln;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 05.02.12
 * Time: 17:05
 */
public class ServiceHelper {
    @Inject
    Context context;

    ReceiversMap receivers;

    public ServiceHelper() {
        this.receivers = new ReceiversMap();
    }

    public static class ReceiversMap implements Parcelable {
        HashMap<Integer, RequestHandler> map;

        public ReceiversMap() {
            this.map = new HashMap<Integer, RequestHandler>();
        }

        public ReceiversMap(Parcel in) {
            map = new HashMap<Integer, RequestHandler>();
            readFromParcel(in);
        }

        public void put(Integer ticket, RequestHandler obj) {
            map.put(ticket, obj);
        }

        public RequestHandler get(Integer ticket) {
            return map.get(ticket);
        }

        public Set<Integer> keySet() {
            return map.keySet();
        }

        public void remove(Integer ticket) {
            map.remove(ticket);
        }

        public boolean containsKey(Integer ticket) {
            return map.containsKey(ticket);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(map.size());

            for (Integer ticket : map.keySet()) {
                parcel.writeInt(ticket);
                parcel.writeValue(map.get(ticket));
            }
        }

        public void readFromParcel(Parcel in) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                map.put(in.readInt(), (RequestHandler) in.readValue(RequestHandler.class.getClassLoader()));
            }
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public ReceiversMap createFromParcel(Parcel in) {
                return new ReceiversMap(in);
            }

            public ReceiversMap[] newArray(int size) {
                return new ReceiversMap[size];
            }
        };
    }

    public void onActivityRestoreInstance(@Observes OnRestoreInstanceEvent event) {
        Ln.d("Called onCreate in onActivityCreated " + event.toString());

        Bundle b = event.getRestoredInstance();
        if (null != b && b.containsKey("receivers")) {
            receivers = (ReceiversMap) b.getParcelable("receivers");
        }

        for (Integer ticket : receivers.keySet()) {
            RequestHandler handler = receivers.get(ticket);

            try {
                Constructor[] constructors = handler.getClass().getDeclaredConstructors();
                Object[] params = new Object[1];
                params[0] = context;

                RequestHandler requestHandler = (RequestHandler) constructors[0].newInstance(params);
                receivers.put(ticket, requestHandler);
            } catch (InstantiationException e) {
                Ln.e(e);
            } catch (IllegalAccessException e) {
                Ln.e(e);
            } catch (InvocationTargetException e) {
                Ln.e(e);
            }
        }
    }

    public void onActivitySaveInstance(@Observes OnSaveInstanceEvent event) {
        Bundle b = event.getSavedInstance();
        b.putParcelable("receivers", receivers);
    }

    public int startService(Intent i, RequestHandler handler) {
        Ln.d("Start service");
        int ticket = i.filterHashCode();
        if (receivers.containsKey(ticket))
            return -1;

        receivers.put(ticket, handler);

        i.putExtra(Constants.EXTRA_STATUS_RECEIVER, new ApiResultReceiver(new Handler(), ticket) {
            @Override
            protected void onReceiveResult(int rc, Bundle b, int ticket) {
                super.onReceiveResult(rc, b, ticket);

                if (!receivers.containsKey(ticket))
                    return;

                switch (rc) {
                    case ApiService.STATUS_RUNNING:
                        receivers.get(ticket).onRequestRunning(b);
                        break;
                }
            }
        });

        Ln.d("Start new thread");
        context.startService(i);
        return ticket;
    }
}