package com.bigman.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {

    static final String APP = "com.android.application"
    static final String LIBRARY = "com.android.library"

    @Override
    void apply(Project project) {
        println("hello world 1 MyPlugin " + project.name)
        if (!(project.plugins.hasPlugin(APP) || project.plugins.hasPlugin(LIBRARY))) {
            throw new IllegalArgumentException(
                    'ResTools gradle plugin can only be applied to android projects.')
        }
        def android=project.extensions.getByType(AppExtension)
        def transform=new RegisterTransform(project)
        android.registerTransform(transform)

        println("hello world 2 MyPlugin " + project.name)
    }
}
