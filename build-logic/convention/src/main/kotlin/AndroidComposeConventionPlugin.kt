import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.cochelper.buildlogic.lib
import com.cochelper.buildlogic.libs
import com.cochelper.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType

/** Enables Compose on an application or library module that already has its Android plugin applied. */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("kotlin-compose"))

        extensions.findByType<ApplicationExtension>()?.buildFeatures?.compose = true
        extensions.findByType<LibraryExtension>()?.buildFeatures?.compose = true

        dependencies {
            add("implementation", platform(libs.lib("androidx-compose-bom")))
            add("implementation", libs.lib("androidx-compose-ui"))
            add("implementation", libs.lib("androidx-compose-ui-graphics"))
            add("implementation", libs.lib("androidx-compose-ui-tooling-preview"))
            add("implementation", libs.lib("androidx-compose-material3"))
            add("implementation", libs.lib("androidx-compose-material-icons-core"))
            add("debugImplementation", libs.lib("androidx-compose-ui-tooling"))
        }
    }
}
