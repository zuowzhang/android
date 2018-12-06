package com.zuowzhang.xlib.asmdemo;

import android.app.Application;
import android.util.Log;

import com.zuowzhang.xlib.api.TraceHelper;
import com.zuowzhang.xlib.api.Tracer;

import java.util.Map;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TraceHelper.init(new Tracer() {
            @Override
            public void trace(String id, Map<String, String> prop) {
                Log.i("Tracer", "id = " + id);
                if (prop != null) {
                    for (Map.Entry<String, String> entry : prop.entrySet()) {
                        Log.i("Tracer", entry.getKey() + " -> " + entry.getValue());
                    }
                }
            }
        });
    }
}
