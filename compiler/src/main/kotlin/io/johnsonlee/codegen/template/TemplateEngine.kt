package io.johnsonlee.codegen.template

import io.johnsonlee.codegen.model.Model
import java.io.Writer

interface TemplateEngine {

    val name: String

    val extension: String

    fun render(template: String, model: Model, writer: Writer): Writer

}