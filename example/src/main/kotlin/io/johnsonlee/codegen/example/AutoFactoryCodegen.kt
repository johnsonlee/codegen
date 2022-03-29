package io.johnsonlee.codegen.example

import com.google.auto.service.AutoService
import io.johnsonlee.codegen.compiler.CodegenProcessor
import io.johnsonlee.codegen.compiler.Language
import io.johnsonlee.codegen.compiler.asElement
import io.johnsonlee.codegen.compiler.asTypeElement
import io.johnsonlee.codegen.compiler.error
import io.johnsonlee.codegen.compiler.getAnnotationMirror
import io.johnsonlee.codegen.compiler.value
import io.johnsonlee.codegen.model.Model
import io.johnsonlee.codegen.template.TemplateEngine
import io.johnsonlee.codegen.template.mustache.MustacheEngine
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

@AutoService(Processor::class)
class AutoFactoryCodegen : CodegenProcessor<AutoFactory>() {

    private val mustache: TemplateEngine by lazy(::MustacheEngine)

    override val engine: TemplateEngine = mustache

    override fun onProcessing(
        processingEnv: ProcessingEnvironment,
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ) {
        roundEnv.onEachAnnotatedElement { element ->
            val implementation = element.asTypeElement()
            val mirror = element.getAnnotationMirror<AutoFactory>()
            mirror.value.takeIf(Set<DeclaredType>::isNotEmpty)?.map(DeclaredType::asTypeElement)?.onEach { api ->
                if (checkImplementation(implementation, api)) {
                    generateFactory(implementation, mirror)
                } else {
                    processingEnv.error(
                        "${implementation.qualifiedName} does not implement ${api.qualifiedName}",
                        element,
                        mirror
                    )
                }
            } ?: processingEnv.error("No interface provided for element!", element, mirror)
        }
    }

    private fun checkImplementation(implementation: TypeElement, api: TypeElement): Boolean {
        val verify: String? by processingEnv.options.withDefault { null }
        if (verify == null || !java.lang.Boolean.parseBoolean(verify)) {
            return true
        }
        return implementation.isSubtypeOf(api)
    }

    private fun generateFactory(implementation: TypeElement, mirror: AnnotationMirror) {
        val constructors = implementation.enclosedElements.filter {
            it.kind == ElementKind.CONSTRUCTOR
        }.takeIf {
            it.size <= 1
        }?.map {
            it as ExecutableElement
        } ?: return processingEnv.error(
            "Too many constructors: ${implementation.qualifiedName}",
            implementation,
            mirror
        )
        val params = constructors.singleOrNull()?.parameters
        val argc = params?.count() ?: 0
        val args = params?.mapIndexed { i, it ->
            mapOf(
                "type" to it.asType(),
                "typeErasure" to processingEnv.typeUtils.erasure(it.asType()),
                "isInterface" to (it.asElement().kind == ElementKind.INTERFACE),
                "isLast" to (i + 1 == argc)
            )
        } ?: emptyList()
        generate(
            "template/AutoFactory",
            AutoFactoryModel(implementation.qualifiedName.toString(), args),
            Language.KOTLIN
        )
    }
}

internal data class AutoFactoryModel(
    val implementation: String,
    val args: List<Map<String, Any?>> = emptyList()
) : Model {

    override val qualifiedName: String = "${PKG_GENERATED}.${implementation.substringAfterLast('.')}_Factory"

    override val simpleName: String = qualifiedName.substringAfterLast(".")

    override val references: Set<String> = setOf(implementation)

    override val packageName: String = qualifiedName.substringBeforeLast(".")

}