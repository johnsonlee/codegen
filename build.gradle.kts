import org.gradle.api.Project.DEFAULT_VERSION

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("io.johnsonlee:sonatype-publish-plugin:1.5.6")
    }
}

group = "io.johnsonlee.codegen"
version = project.properties["version"]?.takeIf { it != DEFAULT_VERSION } ?: "1.0.0-SNAPSHOT"
