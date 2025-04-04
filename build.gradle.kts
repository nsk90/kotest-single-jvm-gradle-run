import ru.nsk.registerRunKotestInSingleJvmTask

plugins {
    //alias(libs.plugins.android.application) version libs.versions.agp apply false
    //alias(libs.plugins.android.library) version libs.versions.agp apply false
    alias(libs.plugins.kotlin.android)  apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    id(libs.plugins.android.application.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    //id(libs.plugins.kotlin.android.get().pluginId) apply false
    //alias(libs.plugins.kotlin.compose) apply false
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
registerRunKotestInSingleJvmTask(rootProject)