// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    kotlin("jvm") version "2.0.20" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "2.0.20"
    id("org.jetbrains.kotlin.kapt") version "1.9.22"
    val room_version = "2.6.1"
    id("androidx.room") version "$room_version" apply false
}