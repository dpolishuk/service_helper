package com.dp.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.inject.Inject;
import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.util.Ln;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 05.02.12
 * Time: 17:05
 */
public class ServiceHelper {
    @Inject
    Context context;

    ReceiversMap receivers;

    class ReceiversMap extends ConcurrentHashMap<Integer, RequestHandler> implements Serializable {
    }

    public ServiceHelper() {
        this.receivers = new ReceiversMap();
    }

    public void onActivityCreated(@Observes OnCreateEvent onCreateEvent) {
        Ln.d("Called onCreate in onActivityCreated " + onCreateEvent.toString());

        Bundle b = onCreateEvent.getSavedInstanceState();
        if (null != b && b.containsKey("receivers"))
            receivers = (ReceiversMap) b.getSerializable("receivers");

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
        b.putSerializable("receivers", receivers);
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