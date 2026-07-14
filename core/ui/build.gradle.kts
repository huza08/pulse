plugins {
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "app.pulse.core.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.core.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.shimmer)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.fonts)
    implementation(libs.compose.material3)
    implementation(libs.palette)

    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}

kotlin {
    // jvmToolchain(libs.versions.jvm.get().toInt())

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.addAll(
            "-Xcontext-parameters"
        )
    }
}
