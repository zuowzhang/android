package com.zuowzhang.xlib.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodVisitor extends AdviceAdapter {
    boolean isMatchingAnnotation = false
    Map<String, String> annotationValues = new HashMap<>()
    boolean hasReturn
    private int startTimeId = -1

    protected TraceMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM5, mv, access, name, desc)
        hasReturn = !desc.endsWith("V")
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        if (isMatchingAnnotation && annotationValues.get("cost")) {
            startTimeId = newLocal(Type.LONG_TYPE)
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J",false)
            mv.visitVarInsn(LSTORE, startTimeId)
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        if (isMatchingAnnotation) {
            if (hasReturn) {
                mv.visitInsn(Opcodes.DUP)
                mv.visitLdcInsn(annotationValues.get("id"))
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/zuowzhang/xlib/api/TraceHelper",
                        "trace",
                        "(Ljava/util/Map;Ljava/lang/String;)V",
                        false)
            } else {
                if (annotationValues.get("cost")) {
                    int durationId = newLocal(Type.LONG_TYPE)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J",false)
                    mv.visitVarInsn(LLOAD, startTimeId)
                    mv.visitInsn(LSUB)
                    mv.visitVarInsn(LSTORE, durationId)
                    mv.visitLdcInsn(annotationValues.get("id"))
                    mv.visitVarInsn(LLOAD, durationId)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/zuowzhang/xlib/api/TraceHelper",
                            "trace",
                            "(Ljava/lang/String;J)V",
                            false)
                } else {
                    mv.visitLdcInsn(annotationValues.get("id"))
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/zuowzhang/xlib/api/TraceHelper",
                            "trace",
                            "(Ljava/lang/String;)V",
                            false)
                }
            }
        }
    }


    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        println("visitAnnotation:$desc")
        if (desc == "Lcom/zuowzhang/xlib/annotation/Trace;") {
            isMatchingAnnotation = true
            return new TraceAnnotationVisitor(super.visitAnnotation(desc, visible), annotationValues)
        }
        return super.visitAnnotation(desc, visible)
    }
}