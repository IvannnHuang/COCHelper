plugins {
    alias(libs.plugins.cochelper.android.library)
    alias(libs.plugins.cochelper.hilt)
}

android {
    namespace = "com.cochelper.core.gamedata"
}

dependencies {
    api(project(":core:model"))
}
