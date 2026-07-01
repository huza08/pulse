plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.compose") version "1.11.1"
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    implementation(projects.providers.innertube)
    implementation(projects.providers.kugou)
    implementation(projects.providers.lrclib)
    implementation(projects.providers.piped)
    implementation(projects.providers.github)
    implementation(projects.providers.sponsorblock)
    implementation(projects.providers.translate)
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
