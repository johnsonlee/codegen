package io.johnsonlee.codegen.compiler

interface Output {
    val extension: String
}

sealed class Source(override val extension: String) : Output {
    object Java: Source("java")
    object Kotlin: Source("kt")
    object KotlinScript: Source("kts")
    object Groovy: Source("groovy")
}

interface Resource : Output {
    val prefix: String
}