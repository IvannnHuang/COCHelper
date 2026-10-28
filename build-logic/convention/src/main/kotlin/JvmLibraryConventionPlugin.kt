import com.cochelper.buildlogic.AndroidConfig
import com.cochelper.buildlogic.lib
import com.cochelper.buildlogic.libs
import com.cochelper.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/** Pure Kotlin module (no Android): game logic that should be unit-tested on the JVM. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("kotlin-jvm"))

        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = AndroidConfig.JAVA_VERSION
            targetCompatibility = AndroidConfig.JAVA_VERSION
        }
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions.jvmTarget.set(JvmTarget.fromTarget(AndroidConfig.JAVA_VERSION.toString()))
        }

        dependencies {
            add("testImplementation", libs.lib("junit"))
        }
    }
}
