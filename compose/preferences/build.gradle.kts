plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.pulse.compose.preferences"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    // jvmToolchain(libs.versions.jvm.get().toInt())

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)

    implementation(libs.core.ktx)

    implementation(libs.kotlin.coroutines)
    api(libs.ktor.serialization.json)

    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}
