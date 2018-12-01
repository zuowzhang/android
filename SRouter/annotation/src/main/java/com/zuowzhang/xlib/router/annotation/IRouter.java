package com.zuowzhang.xlib.router.annotation;

public interface IRouter {
    void init();

    Class<?> match(String path);
}
