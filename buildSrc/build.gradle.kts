plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.8.0")
}
buildscript {
    dependencies {
        // see https://stackoverflow.com/a/71135974
      //  classpath("com.android.tools.build:gradle:8.8.0")
    }
}