package com.zuowzhang.xlib.router.demo;

import java.io.Serializable;

public class Student implements Serializable {
    String name;
    int age;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
