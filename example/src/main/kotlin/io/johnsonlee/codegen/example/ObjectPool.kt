package io.johnsonlee.codegen.example

object ObjectPool {

    @JvmStatic
    fun <T> get(type: Class<T>): T = TODO()

    inline fun <reified T> get(): T = get(T::class.java)

}