package com.zuowzhang.xlib.asmdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zuowzhang.xlib.annotation.Trace;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    @Trace(id="3", cost = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        test01();
        test02();
        test03();
    }


    @Trace(id="1")
    private Map<String, String> test() {
        Log.i("MainActivity", "test");
        Map<String, String> prop = new HashMap<>();
        prop.put("k1", "v1");
        return prop;
    }

    @Trace(id="2")
    private void test01() {

    }

    @Trace(id="4", cost = true)
    private void test02() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Trace(id="5", cost = true)
    private void test03() {

    }
}
