package io.johnsonlee.codegen.template.velocity

import io.johnsonlee.codegen.model.Model
import io.johnsonlee.codegen.template.TemplateEngine
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.Writer
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty0
import kotlin.reflect.KVisibility

class VelocityEngine : TemplateEngine {

    override val name: String = "velocity"

    override val extension: String = "vm"

    init {
        Velocity.init()
    }

    override fun render(template: String, model: Model, writer: Writer): Writer {
        val context = VelocityContext(model.toMap())
        Velocity.mergeTemplate(template, "UTF-8", context, writer)
        return writer
    }

}

private fun Model.toMap(): Map<String, Any?> = javaClass.kotlin.members.filter {
    it.visibility == KVisibility.PUBLIC
}.mapNotNull { member ->
    when (member) {
        is KProperty0 -> member.name to member.get()
        is Function<*> -> member.parameters.takeIf(List<KParameter>::isEmpty)?.let {
            member.name to member.call()
        }
        else -> null
    }
}.toMap()