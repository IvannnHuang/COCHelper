import com.cochelper.buildlogic.lib
import com.cochelper.buildlogic.libs
import com.cochelper.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/** A feature module: Compose UI + Hilt ViewModels + a type-safe navigation route. */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cochelper.android.library")
        pluginManager.apply("cochelper.android.compose")
        pluginManager.apply("cochelper.hilt")
        pluginManager.apply(libs.pluginId("kotlin-serialization"))

        dependencies {
            add("implementation", project(":core:ui"))
            add("implementation", project(":core:model"))
            add("implementation", libs.lib("androidx-navigation-compose"))
            add("implementation", libs.lib("androidx-hilt-lifecycle-viewmodel-compose"))
            add("implementation", libs.lib("androidx-lifecycle-runtime-compose"))
            add("implementation", libs.lib("androidx-lifecycle-viewmodel-compose"))
            add("implementation", libs.lib("kotlinx-serialization-json"))
        }
    }
}
