package com.zuowzhang.xlib.router.api;

import android.content.Intent;

import com.zuowzhang.xlib.router.annotation.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteParamInjector {
    private static Map<Class<?>, Class<?>> destClassMappingInjectorClass = new HashMap<>();

    private static Class<?> getInjectorClass(Class<?> destClass) throws ClassNotFoundException {
        Class<?> injectorClass = destClassMappingInjectorClass.get(destClass);
        if (injectorClass == null) {
            injectorClass = Class.forName(destClass.getCanonicalName() +
                    "$$" +
                    Constants.INJECTOR_CLASS_NAME);
            destClassMappingInjectorClass.put(destClass, injectorClass);
        }
        return injectorClass;
    }

    public static void inject(Object target, Intent intent) {
        try {
            Class<?> injectorClass = getInjectorClass(target.getClass());
            Method injectMethod = injectorClass.getDeclaredMethod(
                    Constants.INJECTOR_METHOD_INJECT_NAME,
                    target.getClass(), Intent.class);
            injectMethod.invoke(injectorClass, target, intent);
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

    public static void parseParams(Class<?> destClass, String path, Intent intent) {
        try {
            Class<?> injectorClass = getInjectorClass(destClass);
            Method parseParams = injectorClass.getDeclaredMethod(
                    Constants.INJECTOR_METHOD_PARSER_PARAMS_NAME,
                    String.class,
                    Intent.class);
            parseParams.invoke(injectorClass, path, intent);
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
