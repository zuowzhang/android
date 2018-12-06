package com.zuowzhang.xlib.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class TraceAnnotationVisitor extends AnnotationVisitor {
    Map<String, String> map

    TraceAnnotationVisitor(AnnotationVisitor av, Map<String, String> map) {
        super(Opcodes.ASM5, av)
        this.map = map
    }

    @Override
    void visit(String name, Object value) {
        println("TraceAnnotationVisitor->visit: " + name + " -> " + value)
        map.put(name, value)
        super.visit(name, value)
    }

    @Override
    void visitEnum(String name, String desc, String value) {
        println("TraceAnnotationVisitor->visitEnum: " + name + " -> " + value + " -> " + desc)
        super.visitEnum(name, desc, value)
    }
}