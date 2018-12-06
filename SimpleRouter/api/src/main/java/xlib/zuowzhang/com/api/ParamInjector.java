package xlib.zuowzhang.com.api;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import xlib.zuowzhang.com.annotation.Constants;

public class ParamInjector {
    private static Map<Class<?>, Class<?>> destClassMappingInjectorClass = new HashMap<>();

    private static Class<?> getInjectorClass(Class<?> destClass) throws ClassNotFoundException {
        Class<?> injectorClass = destClassMappingInjectorClass.get(destClass);
        if (injectorClass == null) {
            injectorClass = Class.forName(destClass.getCanonicalName() + "$$" + Constants.PARAM_INJECT_CLASS_NAME);
            destClassMappingInjectorClass.put(destClass, injectorClass);
        }
        return injectorClass;
    }

    public static void inject(Activity activity) {
        try {
            Class<?> injectorClass = getInjectorClass(activity.getClass());
            Method injectMethod = injectorClass.getDeclaredMethod(Constants.PARAM_INJECT_METHOD_INJECT_NAME, activity.getClass());
            injectMethod.invoke(injectorClass, activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void getParamFromPath(Class<?> destClass, String path, Intent intent) {
        try {
            Class<?> injectorClass = getInjectorClass(destClass);
            Method getParamFromPathMethod = injectorClass.getMethod(Constants.PARAM_INJECT_METHOD_GET_PARAM_FROM_PATH_NAME, String.class, Intent.class);
            getParamFromPathMethod.invoke(injectorClass, path, intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
