package com.zuowzhang.xlib.api;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TraceHelper {
    private static Tracer sTracer;

    public static void init(Tracer tracer) {
        sTracer = tracer;
    }

    public static void trace(String id) {
        if (sTracer != null) {
            sTracer.trace(id, null);
        }
    }

    public static void trace(String id, long cost) {
        if (sTracer != null) {
            Map<String, String> map = new HashMap<>();
            map.put("cost", String.valueOf(cost));
            sTracer.trace(id, map);
        }
    }

    public static void trace(Map<String, String> prop, String id) {
        if (sTracer != null) {
            sTracer.trace(id, prop);
        }
    }

    public static void test() {
        Log.i("TraceHelper", "test success");
    }

    public static void println(String msg) {
        Log.i("TraceHelper", msg);
    }
}
