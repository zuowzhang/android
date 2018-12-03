package com.zuowzhang.xlib.router.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zuowzhang.xlib.router.annotation.Param;
import com.zuowzhang.xlib.router.annotation.Route;
import com.zuowzhang.xlib.router.api.RouteParamInjector;
import com.zuowzhang.xlib.router.api.RoutePayload;
import com.zuowzhang.xlib.router.api.RouterManager;

@Route(path = "/activity/second")
public class SecondActivity extends Activity {
    private static final String tag = "SecondActivity";

    Button go2Third;

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
        go2Third = findViewById(R.id.go2Third);
        go2Third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.get().go(new RoutePayload.Builder("router://my/third?ch=T")
                        .action("myaction")
                        .addParam("name", "li")
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());
            }
        });
        RouteParamInjector.inject(this, getIntent());
        Log.i(tag, "name = " + name + "; \nage = " + age + "; \nch = " + ch + "; \nstudent = " + student);
    }
}
