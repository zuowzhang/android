package com.zuowzhang.xlib.plugin.util

class TextUtil {
    static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }
}