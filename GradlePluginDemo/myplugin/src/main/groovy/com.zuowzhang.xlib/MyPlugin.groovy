package com.zuowzhang.xlib

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println(project.name + "in MyPlugin...")
        //扩展extension
        project.extensions.create('ext1', Ext1)

        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new MyTransform(project))

        project.task('readExtension') << {
            def ext1 = project['ext1']

            println("ext1.name = " + ext1.name)
            println("ext1.number = " + ext1.number)
        }
    }
}