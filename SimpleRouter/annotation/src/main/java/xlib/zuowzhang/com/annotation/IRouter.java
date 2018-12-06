package xlib.zuowzhang.com.annotation;

public interface IRouter {
    void init();

    Class<?> match(String path);
}
