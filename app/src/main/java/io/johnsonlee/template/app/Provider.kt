package io.johnsonlee.template.app

interface Provider<T> {

    fun get(): T

}