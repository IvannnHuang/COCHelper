plugins {
    alias(libs.plugins.cochelper.android.feature)
}

android {
    namespace = "com.cochelper.feature.profile"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
}
