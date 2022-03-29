package io.johnsonlee.codegen.compiler

import com.google.auto.common.MoreTypes
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

fun DeclaredType.asTypeElement(): TypeElement = MoreTypes.asTypeElement(this)
