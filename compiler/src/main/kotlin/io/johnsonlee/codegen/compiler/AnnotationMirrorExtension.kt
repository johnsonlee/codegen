package io.johnsonlee.codegen.compiler

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreTypes
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleAnnotationValueVisitor8

private object AnnotationValueVisitor : SimpleAnnotationValueVisitor8<Set<DeclaredType>, Void>() {
    override fun visitType(typeMirror: TypeMirror?, v: Void?): Set<DeclaredType> {
        return setOf(MoreTypes.asDeclared(typeMirror))
    }

    override fun visitArray(values: MutableList<out AnnotationValue>, v: Void?): Set<DeclaredType> {
        return values.map {
            it.accept(this, null)
        }.flatten().toSet()
    }
}

val AnnotationMirror.value: Set<DeclaredType>
    get() = AnnotationMirrors.getAnnotationValue(this, "value").accept(AnnotationValueVisitor, null)