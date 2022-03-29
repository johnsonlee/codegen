package io.johnsonlee.codegen.example

import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class AutoFactory(
    val value: KClass<*>
)
