import org.gradle.api.Project.DEFAULT_VERSION

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${embeddedKotlinVersion}")
        classpath("io.johnsonlee:sonatype-publish-plugin:1.6.2")
    }
}

group = "io.johnsonlee.codegen"
version = project.properties["version"]?.takeIf { it != DEFAULT_VERSION } ?: "1.0.0-SNAPSHOT"

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        google()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xskip-metadata-version-check")
            jvmTarget = "1.8"
        }
    }

}
