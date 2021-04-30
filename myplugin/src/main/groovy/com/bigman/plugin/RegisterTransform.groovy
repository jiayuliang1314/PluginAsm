//package com.bigman.plugin
//
//import com.android.build.api.transform.QualifiedContent
//import com.android.build.api.transform.Transform
//import com.android.build.api.transform.TransformException
//import com.android.build.api.transform.TransformInvocation
//import com.android.build.gradle.internal.pipeline.TransformManager
//import org.gradle.api.Project
//
//class RegisterTransform extends Transform {
//    Project mProject
//
//    RegisterTransform(Project project) {
//        mProject = project
//    }
//
//    @Override
//    String getName() {
//        return "auto-register"
//    }
//
//    @Override
//    Set<QualifiedContent.ContentType> getInputTypes() {
//        return TransformManager.CONTENT_CLASS
//    }
//
//    @Override
//    Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT
//    }
//
//    @Override
//    boolean isIncremental() {
//        //支持增量变异
//        return true
//    }
//
//    @Override
//    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        super.transform(transformInvocation)
//        mProject.logger.warn("start auto-register transform")
//    }
//}