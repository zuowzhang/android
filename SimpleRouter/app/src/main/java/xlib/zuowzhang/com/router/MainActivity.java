package xlib.zuowzhang.com.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import xlib.zuowzhang.com.annotation.Param;
import xlib.zuowzhang.com.annotation.Router;
import xlib.zuowzhang.com.api.ParamInjector;
import xlib.zuowzhang.com.api.Payload;
import xlib.zuowzhang.com.api.RouterManager;

@Router(path = "/main")
public class MainActivity extends AppCompatActivity {

    @Param
    String name;

    Button go2Second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParamInjector.inject(this);
        go2Second = findViewById(R.id.go2Second);
        go2Second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.get().go(new Payload.Builder("/second?age=17&f=10.8&ch=3")
                        .addParam("name", "zhangsan")
                        .build());
            }
        });
    }
}
