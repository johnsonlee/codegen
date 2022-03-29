package io.johnsonlee.codegen.compiler

import com.google.auto.common.MoreTypes
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

fun VariableElement.asElement(): Element = MoreTypes.asElement(this.asType())

