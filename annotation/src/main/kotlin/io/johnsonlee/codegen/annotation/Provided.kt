package io.johnsonlee.codegen.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Provided(
    val value: KClass<*>
)
