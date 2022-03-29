package io.johnsonlee.codegen.template.mustache

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import io.johnsonlee.codegen.model.Model
import io.johnsonlee.codegen.template.TemplateEngine
import java.io.Writer

class MustacheEngine : TemplateEngine {

    private val mustacheFactory: MustacheFactory by lazy {
        object : DefaultMustacheFactory() {
            override fun encode(value: String, writer: Writer) {
                writer.write(value)
            }
        }
    }

    override val name: String = "mustache"

    override val extension: String = name

    override fun render(template: String, model: Model, writer: Writer): Writer {
        return mustacheFactory.compile(template).execute(writer, model)
    }


}