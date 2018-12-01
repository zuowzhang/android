package com.zuowzhang.xlib.router.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zuowzhang.xlib.router.annotation.Param;
import com.zuowzhang.xlib.router.annotation.Route;
import com.zuowzhang.xlib.router.api.RouteParamInjector;

@Route(path = "/service/demo")
public class DemoService extends Service {

    private static final String tag = "DemoService";

    @Param
    String name;

    @Param
    int age;

    @Param
    char ch;

    @Param
    Student student;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RouteParamInjector.inject(this, intent);
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
