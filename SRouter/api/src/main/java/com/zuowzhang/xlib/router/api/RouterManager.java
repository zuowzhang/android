package com.zuowzhang.xlib.router.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.zuowzhang.xlib.router.annotation.Constants;
import com.zuowzhang.xlib.router.annotation.IRouter;

import java.util.ArrayList;
import java.util.List;

public class RouterManager {
    private List<IRouter> routers;
    private boolean initialized = false;
    private Context appContext;

    private static volatile RouterManager sInstance;

    private RouterManager() {
        routers = new ArrayList<>();
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
                Class<?> appRouterClass =
                        Class.forName(Constants.PACKAGE_NAME_GEN + "." + Constants.APP_ROUTER_CLASS_NAME);
                IRouter appRouter = (IRouter) appRouterClass.newInstance();
                appRouter.init();
                routers.add(appRouter);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void go(RoutePayload routePayload) {
        Class<?> destClass = null;
        for (IRouter router : routers) {
            destClass = router.match(routePayload.getPath());
            if (destClass != null) {
                break;
            }
        }
        Intent intent;
        if (destClass != null) {
            intent = new Intent(appContext, destClass);
            RouteParamInjector.parseParams(destClass, routePayload.getPath(), intent);
        } else {
            String action = routePayload.getAction();
            if (TextUtils.isEmpty(action)) {
                action = Intent.ACTION_VIEW;
            }
            intent = new Intent(action);
            intent.setData(Uri.parse(routePayload.getPath()));
        }
        if (routePayload.getFlags() != 0) {
            intent.setFlags(routePayload.getFlags());
        }
        if (routePayload.getParams().size() > 0) {
            intent.putExtras(routePayload.getParams());
        }
        switch (routePayload.getType()) {
            case ACTIVITY:
                if (routePayload.getRequestActivity() != null) {
                    routePayload.getRequestActivity().startActivityForResult(intent, routePayload.getRequestCode());
                } else {
                    appContext.startActivity(intent);
                }
                break;
            case SERVICE:
                appContext.startService(intent);
                break;
            case BROADCAST:
                appContext.sendBroadcast(intent);
                break;
        }
    }
}
