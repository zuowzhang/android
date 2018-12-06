package xlib.zuowzhang.com.api;

import android.content.Context;
import android.content.Intent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import xlib.zuowzhang.com.annotation.Constants;
import xlib.zuowzhang.com.annotation.IRouter;

public class RouterManager {
    private Context appContext;
    private List<IRouter> routers = new ArrayList<>();
    private boolean initialized = false;
    private static volatile RouterManager sInstance;


    private RouterManager() {
    }

    public static RouterManager get() {
        if (sInstance == null) {
            synchronized (RouterManager.class) {
                if (sInstance == null) {
                    sInstance = new RouterManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        if (!initialized) {
            initialized = true;
            appContext = context.getApplicationContext();
            try {
                Class<?> appRouterClass = Class.forName(Constants.ROUTER_PACKAGE_NAME + "." + Constants.APP_ROUTER_CLASS_NAME);
                IRouter router = (IRouter) appRouterClass.newInstance();
                router.init();
                routers.add(router);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void go(Payload payload) {
        Class<?> destClass = null;
        for (IRouter router : routers) {
            destClass = router.match(payload.getPath());
            if (destClass != null) {
                break;
            }
        }
        if (destClass != null) {
            Intent intent = new Intent(appContext, destClass);
            if (payload.getFlags() != 0) {
                intent.setFlags(payload.getFlags());
            }

            if (payload.getParams().size() > 0) {
                intent.putExtras(payload.getParams());
            }
            appContext.startActivity(intent);
        } else {

        }

    }
}
