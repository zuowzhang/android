package xlib.zuowzhang.com.router;

import android.app.Application;

import xlib.zuowzhang.com.api.RouterManager;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RouterManager.get().init(this);
    }
}
