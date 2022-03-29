package io.johnsonlee.codegen.example

interface Factory<T> {

    val type: Class<T>

    fun newInstance(pool: ObjectPool): T

}