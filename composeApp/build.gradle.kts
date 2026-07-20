import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    // expect/actual classes (Auth, Notifier, SecureStore, LocaleSettingsNavigator
    // in platform/) are the whole point of the platform layer's architecture —
    // opting in rather than living with the warning on every compile.
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iosX64 (Intel simulator) is intentionally omitted: several current KMP
    // libraries (Compose Multiplatform, AndroidX Lifecycle KMP) no longer
    // publish klibs for it now that Apple Silicon is the baseline. Revisit
    // only if Intel-Mac CI/dev support becomes a real requirement.
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target: KotlinNativeTarget ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.navigation.compose)

                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)

                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.datastore)
                implementation(libs.androidx.datastore.preferences)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.android)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.android)
                implementation(libs.androidx.work.runtime.ktx)
                implementation(libs.androidx.credentials)
                implementation(libs.androidx.credentials.play.services.auth)
                implementation(libs.googleid)
            }
        }
        // The default hierarchy template doesn't reliably detect targets
        // declared via the forEach loop above, so iosMain is wired manually
        // (kotlin.mpp.applyDefaultHierarchyTemplate=false in gradle.properties).
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    namespace = "app.tally"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
