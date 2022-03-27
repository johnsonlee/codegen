package io.johnsonlee.codegen.model

interface Model {
    val qualifiedName: String
    val simpleName: String
    val packageName: String
    val references: Set<String>
}