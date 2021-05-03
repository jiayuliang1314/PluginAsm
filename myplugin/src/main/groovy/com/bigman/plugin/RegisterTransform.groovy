package com.bigman.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile

class RegisterTransform extends Transform {
    Project mProject
    File needInsertFile
    String needInsertClassNameLeft = "com/kite/testplugin/CategoryManager"
    String insertInterfaceName = "com/kite/testplugin/ICategory"

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
                        scanJar(jarInput.file)
                }

                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        mProject.logger.warn("dir file ---" + directoryInput.file.absolutePath)

                        String root = directoryInput.file.absolutePath
                        if (!root.endsWith(File.separator)) {
                            root += File.separator
                        }
                        mProject.logger.warn("root--" + root)
                        directoryInput.file.eachFileRecurse { File file ->
                            def path = file.absolutePath.replace(root, '')
                            mProject.logger.warn("path--" + path)

                            if (file.isFile()) {
                                def entryName = path
                                entryName = entryName.substring(0, entryName.lastIndexOf('.'))
                                mProject.logger.warn("file--" + file.absolutePath)
                                if (entryName.endsWith(needInsertClassNameLeft)) {
                                    needInsertFile = file
                                    mProject.logger.error("this class is our class name==" + entryName)
                                    return
                                }

                                if (path.endsWith(".class")) {
                                    asmScanClass(file)
                                }
                            }
                        }
                }
        }
        //代码插入
        if (needInsertFile != null) {
            generateCodeIntoClassFile(needInsertFile)
        }
    }

    private void generateCodeIntoClassFile(File file) {
        def optClass = new File(file.getParent(), file.name + ".opt")
        FileInputStream inputStream = new FileInputStream(file)
        FileOutputStream outputStream = new FileOutputStream(optClass)
        def bytes = doGenerateCode(inputStream)
        outputStream.write(bytes)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        optClass.renameTo(file)
    }

    private byte[] doGenerateCode(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        MyClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
        return cw.toByteArray()
    }


    private void asmScanClass(File file) {
        InputStream inputStream = file.newInputStream()
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw, file.absolutePath)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    class ScanClassVisitor extends ClassVisitor {
        String filePath

        ScanClassVisitor(int api, ClassVisitor cv, String path) {
            super(api, cv)
            this.filePath = path
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)


            if (interfaces != null) {
                interfaces.each { itName ->
                    if (itName == insertInterfaceName)
                        mProject.logger.error("this class is our interface name==" + name)
                }
            }
        }
    }

    private void scanJar(File file) {
        def jarFile = new JarFile(file)
        def enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) {
            def jarEntry = enumeration.nextElement()
            def entryName = jarEntry.name
            if (entryName.startsWith("androidx/") || entryName.startsWith("android/") /*|| entryName.startsWith("META-INF/")*/) {
                break
            }
            if (entryName.startsWith("META-INF/")) {
                continue
            }
            mProject.logger.error("jar---" + file.absolutePath + " class --- " + entryName)
        }
        if (jarFile != null) {
            jarFile.close()
        }
    }
}