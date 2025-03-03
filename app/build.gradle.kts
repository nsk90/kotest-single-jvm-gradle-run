
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.nsk.junitmultimoduletestapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.nsk.junitmultimoduletestapplication"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
       // compose = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // implementation(libs.androidx.activity.compose)
    //implementation(platform(libs.androidx.compose.bom))
    //implementation(libs.androidx.ui)
    //implementation(libs.androidx.ui.graphics)
    //implementation(libs.androidx.ui.tooling.preview)
    //implementation(libs.androidx.material3)
    //testImplementation(libs.junit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")

    //testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")

    testImplementation("org.junit.platform:junit-platform-launcher:1.9.3") // to run tests fom jvm
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    //androidTestImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.androidx.espresso.core)
    //androidTestImplementation(platform(libs.androidx.compose.bom))
    //androidTestImplementation(libs.androidx.ui.test.junit4)


    //debugImplementation(libs.androidx.ui.tooling)
    //debugImplementation(libs.androidx.ui.test.manifest)
}