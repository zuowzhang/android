package com.zuowzhang.xlib.router.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.zuowzhang.xlib.router.annotation.Param;
import com.zuowzhang.xlib.router.annotation.Route;
import com.zuowzhang.xlib.router.api.RouteParamInjector;

@Route(path = "/activity/second")
public class SecondActivity extends Activity {
    private static final String tag = "SecondActivity";

    @Param
    String name;

    @Param
    int age;

    @Param
    char ch;

    @Param
    Student student;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        RouteParamInjector.inject(this, getIntent());
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
    }
}
