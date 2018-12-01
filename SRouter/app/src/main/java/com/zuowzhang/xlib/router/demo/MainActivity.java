package com.zuowzhang.xlib.router.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zuowzhang.xlib.router.api.RoutePayload;
import com.zuowzhang.xlib.router.api.RouteType;
import com.zuowzhang.xlib.router.api.RouterManager;


public class MainActivity extends AppCompatActivity {

    private static final String tag = "MainActivity";

    Button go2Second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        go2Second = findViewById(R.id.go2Second);

        go2Second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Student student = new Student();
                student.name = "zzz";
                student.age = 18;

                RouterManager.get().go(new RoutePayload.Builder("/activity/second?ch=A")
                        .addParam("name", "li")
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());

                RouterManager.get().go(new RoutePayload.Builder("/service/demo?ch=S")
                        .type(RouteType.SERVICE)
                        .addParam("name", "li")
                        .addParam("age", 16)
                        .addParam("student", student)
                        .build());
            }
        });
    }
}
