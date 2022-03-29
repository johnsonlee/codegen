package io.johnsonlee.codegen.compiler

import io.johnsonlee.codegen.model.Model
import io.johnsonlee.codegen.template.TemplateEngine
import java.io.File
import java.lang.reflect.ParameterizedType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.FileObject
import javax.tools.StandardLocation

abstract class CodegenProcessor<T : Annotation> : AbstractProcessor() {

    protected abstract val engine: TemplateEngine

    protected val kaptKotlinGenerated: File? by lazy {
        processingEnv.options["kapt.kotlin.generated"]?.let(::File)
    }

    protected val supportedAnnotation: Class<T> by lazy {
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.map {
            it as Class<T>
        }.first()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(supportedAnnotation.typeName)
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        onPreProcessing(processingEnv)
    }

    final override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean = try {
        if (roundEnv.processingOver()) {
            onPostProcessing(processingEnv)
        } else {
            onProcessing(processingEnv, annotations, roundEnv)
        }
        true
    } catch (e: Exception) {
        processingEnv.fatal(e)
        true
    }

    protected open fun onPreProcessing(processingEnv: ProcessingEnvironment) = Unit

    protected open fun onProcessing(
        processingEnv: ProcessingEnvironment,
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ) = Unit

    protected open fun onPostProcessing(processingEnv: ProcessingEnvironment) = Unit

    protected fun <T : Model> generate(template: String, model: T, lang: Language = Language.JAVA) {
        val originatingElements = model.references.map {
            processingEnv.elementUtils.getTypeElement(it)
        }.toTypedArray()
        when (lang) {
            Language.JAVA -> generateJavaSources(model, originatingElements)
            else -> generateSources(lang, model, originatingElements)
        }.openWriter().use { writer ->
            engine.render("${template}.${lang.extension}.${engine.extension.lowercase()}", model, writer)
            writer.close()
        }
    }

    protected fun RoundEnvironment.onEachAnnotatedElement(each: (Element) -> Unit): Set<Element> {
        return getElementsAnnotatedWith(supportedAnnotation).onEach(each)
    }

    protected fun TypeElement.isSubtypeOf(parent: TypeElement): Boolean {
        return processingEnv.typeUtils.isSubtype(this.asType(), parent.asType())
    }

    private fun <T : Model> generateJavaSources(model: T, originatingElements: Array<TypeElement>): FileObject {
        return processingEnv.filer.createSourceFile(model.qualifiedName, *originatingElements)
    }

    private fun <T : Model> generateSources(
        lang: Language,
        model: T,
        originatingElements: Array<TypeElement>
    ): FileObject {
        val pkg = model.qualifiedName.substringBeforeLast('.')
        val name = model.qualifiedName.substringAfterLast('.')
        return processingEnv.filer.createResource(
            StandardLocation.SOURCE_OUTPUT,
            pkg,
            "${name}.${lang.extension}",
            *originatingElements
        )
    }


}
