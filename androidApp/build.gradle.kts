import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(compose.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}

android {
    // Must be distinct from :composeApp's "app.tally" namespace (AGP requires
    // unique namespaces across modules). Manifest component names below are
    // fully-qualified rather than relative, since they resolve against this
    // namespace, not against the actual "app.tally" package the classes live in.
    namespace = "app.tally.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.tally"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = false // flip on in Phase 6 with R8 rules for Room/serialization/Ktor/Koin
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true // BuildConfig.DEBUG gates the Component gallery (debug builds only)
    }
}
