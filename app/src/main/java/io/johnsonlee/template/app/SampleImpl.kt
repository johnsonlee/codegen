package io.johnsonlee.template.app

import android.content.Context
import io.johnsonlee.codegen.example.AutoFactory

@AutoFactory(Sample::class)
class SampleImpl(
    val context: Provider<Context>
) : Sample {
}