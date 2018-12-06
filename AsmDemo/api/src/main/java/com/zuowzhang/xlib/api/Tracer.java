package com.zuowzhang.xlib.api;

import java.util.Map;

public interface Tracer {
    void trace(String id, Map<String, String> prop);
}
