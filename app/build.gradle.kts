plugins {
    alias(libs.plugins.cochelper.android.application)
    alias(libs.plugins.cochelper.android.compose)
    alias(libs.plugins.cochelper.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.cochelper"

    defaultConfig {
        applicationId = "com.cochelper"
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:gamedata"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))

    implementation(project(":feature:profile"))
    implementation(project(":feature:upgrades"))
    implementation(project(":feature:layouts"))
    implementation(project(":feature:scout"))
    implementation(project(":feature:attack"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
