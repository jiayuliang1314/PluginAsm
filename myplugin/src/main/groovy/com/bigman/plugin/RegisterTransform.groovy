package com.bigman.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

import java.util.jar.JarFile

class RegisterTransform extends Transform {
    Project mProject

    RegisterTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "auto-register"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //支持增量变异
        return true
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        mProject.logger.warn("start auto-register transform")
        inputs.each {
            TransformInput input ->
                input.jarInputs.each {
                    JarInput jarInput ->
//                        mProject.logger.warn("jar---" + jarInput.file.absolutePath)
                        scanJar(jarInput.file)
                }

                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        mProject.logger.warn("dir file ---" + directoryInput.file.absolutePath)
                }
        }
    }

    private void scanJar(File file) {
        def jarFile = new JarFile(file)
        def enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) {
            def jarEntry = enumeration.nextElement()
            def entryName = jarEntry.name
            if(entryName.startsWith("androidx/") || entryName.startsWith("android/") || entryName.startsWith("META-INF/")){
                break;
            }
            mProject.logger.error("jar---" + file.absolutePath + " class --- " + entryName)
        }
        if (jarFile != null) {
            jarFile.close()
        }
    }
}