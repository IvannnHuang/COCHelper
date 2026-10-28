import com.cochelper.buildlogic.lib
import com.cochelper.buildlogic.libs
import com.cochelper.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Hilt via KSP. Android modules get the Hilt Gradle plugin; plain JVM modules only get hilt-core. */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("ksp"))
        dependencies {
            add("ksp", libs.lib("hilt-compiler"))
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            dependencies {
                add("implementation", libs.lib("hilt-core"))
            }
        }
        pluginManager.withPlugin("com.android.base") {
            pluginManager.apply(libs.pluginId("hilt"))
            dependencies {
                add("implementation", libs.lib("hilt-android"))
            }
        }
    }
}
