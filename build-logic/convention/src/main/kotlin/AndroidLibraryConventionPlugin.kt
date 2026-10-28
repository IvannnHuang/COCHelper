import com.android.build.api.dsl.LibraryExtension
import com.cochelper.buildlogic.AndroidConfig
import com.cochelper.buildlogic.lib
import com.cochelper.buildlogic.libs
import com.cochelper.buildlogic.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.pluginId("android-library"))

        extensions.configure<LibraryExtension> {
            compileSdk = AndroidConfig.COMPILE_SDK
            defaultConfig {
                minSdk = AndroidConfig.MIN_SDK
            }
            compileOptions {
                sourceCompatibility = AndroidConfig.JAVA_VERSION
                targetCompatibility = AndroidConfig.JAVA_VERSION
            }
        }

        dependencies {
            add("testImplementation", libs.lib("junit"))
        }
    }
}
