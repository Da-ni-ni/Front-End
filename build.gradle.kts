
buildscript {
    repositories {
        google()  // Google Maven 저장소 추가
        mavenCentral()  // Maven Central 저장소 추가
    }
    dependencies {
        // Navigation SafeArgs 플러그인 추가
        val nav_version = "2.8.9"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")

        // Hilt Gradle Plugin 추가
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Hilt 플러그인 설정 (버전 명시)
    id("com.google.dagger.hilt.android") version "2.56.1" apply false

    // KSP 플러그인 설정 (버전 명시)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}
