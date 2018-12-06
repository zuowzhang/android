package com.zuowzhang.xlib.gradleplugindemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {

        trace01();
    }


    @Trace(id="1")
    private void trace01() {
        @Prop Map<String, String> prop = new HashMap<>();
        prop.put("key1", "value1");
    }

    @Trace(id="2")
    public void onClick2(View view) {

    }

}
