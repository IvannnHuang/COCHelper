import java.util.Properties

plugins {
    alias(libs.plugins.cochelper.android.library)
    alias(libs.plugins.cochelper.hilt)
}

// Dev defaults come from the git-ignored local.properties (coc.apiToken, coc.playerTag).
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.cochelper.core.datastore"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "DEFAULT_API_TOKEN", "\"${localProperties.getProperty("coc.apiToken", "")}\"")
        buildConfigField("String", "DEFAULT_PLAYER_TAG", "\"${localProperties.getProperty("coc.playerTag", "")}\"")
    }
}

dependencies {
    api(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
}
