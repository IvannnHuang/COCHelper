plugins {
    alias(libs.plugins.cochelper.android.library)
    alias(libs.plugins.cochelper.hilt)
}

android {
    namespace = "com.cochelper.core.database"
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
