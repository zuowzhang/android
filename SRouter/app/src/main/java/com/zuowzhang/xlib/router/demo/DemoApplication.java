package com.zuowzhang.xlib.router.demo;

import android.app.Application;

import com.zuowzhang.xlib.router.api.RouterManager;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RouterManager.get().init(this);
    }
}
