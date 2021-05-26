package com.bigman.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MyClassVisitor extends ClassVisitor {

    MyClassVisitor(int api, ClassVisitor cv) {
        super(api, cv)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        //要注入的是静态代码块
        if (name == "<clinit>") {
            mv = new MyMethodVisitor(Opcodes.ASM5, mv)
        }
        return mv
    }

    class MyMethodVisitor extends MethodVisitor {
        MyMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv)
        }

        @Override
        void visitInsn(int opcode) {
//            差异代码 我们去掉label和linenumber
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                mv.visitTypeInsn(Opcodes.NEW, "com/kite/testplugin/CategoryA")
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/kite/testplugin/CategoryA", "<init>", "()V", false)
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kite/testplugin/CategoryManager", "register", "(Lcom/kite/testplugin/ICategory;)V", false)
            }
            super.visitInsn(opcode)
        }
    }
}