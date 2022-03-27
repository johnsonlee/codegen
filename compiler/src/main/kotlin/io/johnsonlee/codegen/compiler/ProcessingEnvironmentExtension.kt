package io.johnsonlee.codegen.compiler

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.tools.Diagnostic

fun ProcessingEnvironment.debug(msg: String) {
    if (options.containsKey("debug")) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg)
    }
}

fun ProcessingEnvironment.debug(msg: String, element: Element, annotation: AnnotationMirror) {
    if (options.containsKey("debug")) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg, element, annotation)
    }
}

fun ProcessingEnvironment.warn(msg: String, element: Element, annotation: AnnotationMirror) {
    messager.printMessage(Diagnostic.Kind.WARNING, msg, element, annotation)
}

fun ProcessingEnvironment.error(msg: String, element: Element, annotation: AnnotationMirror) {
    messager.printMessage(Diagnostic.Kind.ERROR, msg, element, annotation)
}

fun ProcessingEnvironment.fatal(e: Throwable) {
    messager.printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: ${e.stackTraceToString()}")
}
