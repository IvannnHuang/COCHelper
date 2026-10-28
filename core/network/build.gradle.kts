plugins {
    alias(libs.plugins.cochelper.android.library)
    alias(libs.plugins.cochelper.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.cochelper.core.network"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)
}
