package io.johnsonlee.codegen.compiler

import com.google.auto.common.MoreElements
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

fun Element.asTypeElement(): TypeElement = MoreElements.asType(this)

inline fun <reified T : Annotation> Element.getAnnotationMirror(): AnnotationMirror? {
    return MoreElements.getAnnotationMirror(this, T::class.java).takeIf {
        it.isPresent
    }?.get()
}
