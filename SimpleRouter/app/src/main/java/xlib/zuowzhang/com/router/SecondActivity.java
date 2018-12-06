package xlib.zuowzhang.com.router;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import xlib.zuowzhang.com.annotation.Param;
import xlib.zuowzhang.com.annotation.Router;
import xlib.zuowzhang.com.api.ParamInjector;
import xlib.zuowzhang.com.api.Payload;
import xlib.zuowzhang.com.api.RouterManager;

@Router(path = "/second")
public class SecondActivity extends Activity {
    @Param
    String name;

    @Param
    int age;

    @Param
    char ch;

    @Param
    float f;

    Button go2Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ParamInjector.inject(this);

        Log.i("SecondActivity", "name = " + name + "; age = " + age);

        go2Main = findViewById(R.id.go2Main);
        go2Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.get().go(new Payload.Builder("/main?name=lisi")
                        .path("/main")
                        .build());
            }
        });
    }
}
