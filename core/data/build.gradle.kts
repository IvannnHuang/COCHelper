plugins {
    alias(libs.plugins.cochelper.android.library)
    alias(libs.plugins.cochelper.hilt)
}

android {
    namespace = "com.cochelper.core.data"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(libs.kotlinx.coroutines.android)
}
