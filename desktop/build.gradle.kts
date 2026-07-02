plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
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
    implementation("org.slf4j:slf4j-simple:2.0.17")

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.sqlite.jdbc)
}

// Cross-platform native binary download task (works on Linux/macOS/Windows)
tasks.register("downloadNativeBinaries") {
    description = "Download static yt-dlp + ffmpeg binaries for bundling"

    doLast {
        val osName = System.getProperty("os.name").lowercase()
        val isWindows = osName.contains("win")

        val runner = if (isWindows) listOf("cmd", "/c", "download-binaries.bat")
            else listOf("bash", "download-binaries.sh")
        val proc = ProcessBuilder(runner)
            .directory(project.projectDir)
            .inheritIO()
            .start()
        val exit = proc.waitFor()
        if (exit != 0) throw GradleException("Binary download failed (exit $exit)")
    }
}

tasks.register("ensureWindowsScript") {
    doLast {
        val nativeDir = file("src/main/resources/native/windows")
        val batFile = file("download-binaries.bat")
        if (!batFile.exists()) {
            batFile.writeText("""@echo off
setlocal
set NATIVE_DIR=src\main\resources\native\windows
if not exist "%NATIVE_DIR%" mkdir "%NATIVE_DIR%"

echo ==^> Downloading yt-dlp.exe for Windows...
curl -fsSL https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe -o "%NATIVE_DIR%\yt-dlp.exe"
echo     OK

echo ==^> Downloading ffmpeg.exe for Windows...
echo This may take a moment (large download)...
set FFMPEG_ZIP=%TEMP%\ffmpeg-static.zip
curl -fsSL https://github.com/BtbN/FFmpeg-Builds/releases/latest/download/ffmpeg-n7.1-latest-win64-gpl-7.1.zip -o "%FFMPEG_ZIP%"
tar -xf "%FFMPEG_ZIP%" -C "%TEMP%" >nul 2>&1
copy /Y "%TEMP%\ffmpeg-n7.1-latest-win64-gpl-7.1\bin\ffmpeg.exe" "%NATIVE_DIR%\ffmpeg.exe" >nul
rmdir /S /Q "%TEMP%\ffmpeg-n7.1-latest-win64-gpl-7.1" 2>nul
del "%FFMPEG_ZIP%" 2>nul
echo     OK

echo.
echo ==^> Done. Binaries in %NATIVE_DIR%/
dir /B "%NATIVE_DIR%"
""")
            logger.warn("Created download-binaries.bat for Windows native binary download.")
        }
    }
}

tasks.named("downloadNativeBinaries") {
    dependsOn("ensureWindowsScript")
}

compose.desktop {
    application {
        mainClass = "app.pulse.desktop.MainKt"
    }
}
