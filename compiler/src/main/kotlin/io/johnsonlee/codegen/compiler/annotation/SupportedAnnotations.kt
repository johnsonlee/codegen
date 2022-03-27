package io.johnsonlee.codegen.compiler.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SupportedAnnotations(
    vararg val value: KClass<out Annotation>
)
