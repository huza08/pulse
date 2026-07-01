plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.compose") version "1.11.1"
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
