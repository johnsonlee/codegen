package io.johnsonlee.codegen.hilt

import com.google.auto.service.AutoService
import io.johnsonlee.codegen.annotation.Provided
import io.johnsonlee.codegen.compiler.CodegenProcessor
import io.johnsonlee.codegen.compiler.Language
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
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

@AutoService(Processor::class)
class ProviderCodegen : CodegenProcessor<Provided>() {

    private val mustache: TemplateEngine by lazy(::MustacheEngine)

    private val interfaces: MutableSet<TypeModel> by lazy(::mutableSetOf)

    override val engine: TemplateEngine = mustache

    override fun onProcessing(
        processingEnv: ProcessingEnvironment,
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ) {
        roundEnv.onEachAnnotatedElement { element ->
            val implementation = element.asTypeElement()
            val mirror = element.getAnnotationMirror<Provided>()
            mirror.value.takeIf(Set<DeclaredType>::isNotEmpty)?.map(DeclaredType::asTypeElement)?.onEach { api ->
                if (checkImplementation(implementation, api)) {
                    interfaces += TypeModel(api.qualifiedName.toString())
                    generateProvider(api, implementation, mirror)
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

    override fun onPostProcessing(processingEnv: ProcessingEnvironment) {
        generate(
            "template/Dsl",
            DslModel(interfaces.map {
                DslItemModel("${PKG}.${it.simpleName}_Provider", it.qualifiedName, it.simpleName)
            }.toSet()),
            Language.KOTLIN
        )
    }

    private fun checkImplementation(implementation: TypeElement, api: TypeElement): Boolean {
        val verify: String? by processingEnv.options.withDefault { null }
        if (verify == null || !java.lang.Boolean.parseBoolean(verify)) {
            return true
        }
        return implementation.isSubtypeOf(api)
    }

    private fun generateProvider(api: TypeElement, implementation: TypeElement, mirror: AnnotationMirror) {
        generate(
            "template/Provider",
            ProviderModel(TypeModel(api.qualifiedName.toString()), TypeModel(implementation.qualifiedName.toString())),
            Language.KOTLIN
        )
    }

}

open class TypeModel(
    val qualifiedName: String,
    val simpleName: String = qualifiedName.substringAfterLast('.')
)

class DslItemModel(
    val providerName: String,
    qualifiedName: String,
    simpleName: String
) : TypeModel(qualifiedName, simpleName)

class DslModel(
    val interfaces: Set<DslItemModel>
): Model {
    override val packageName: String = PKG
    override val simpleName: String = "Dsl"
    override val qualifiedName: String = "${packageName}.${simpleName}"
    override val references: Set<String> = emptySet()
}

class ProviderModel(
    val api: TypeModel,
    val implementation: TypeModel
) : Model {
    override val packageName: String = PKG
    override val qualifiedName: String = "${packageName}.${api.simpleName}_Provider"
    override val simpleName: String = qualifiedName.substringAfterLast('.')
    override val references: Set<String> = emptySet()
}