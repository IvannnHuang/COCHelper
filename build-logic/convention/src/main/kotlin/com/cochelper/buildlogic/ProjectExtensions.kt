package com.cochelper.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.lib(alias: String) = findLibrary(alias).get()

fun VersionCatalog.pluginId(alias: String): String = findPlugin(alias).get().get().pluginId

/** Shared Android settings. AGP 9's built-in Kotlin takes its JVM target from these compile options. */
object AndroidConfig {
    const val COMPILE_SDK = 37
    const val TARGET_SDK = 37
    const val MIN_SDK = 26
    val JAVA_VERSION = JavaVersion.VERSION_17
}
