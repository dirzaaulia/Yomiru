import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.dirzaaulia.yomiru"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dirzaaulia.yomiru"
        minSdk = 29
        targetSdk = 36
        
        // Dynamically compute unique versionCode based on epoch minutes since 2025-01-01
        // or override via VERSION_CODE env variable / versionCode Gradle property
        val envVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull()
        val propVersionCode = (project.findProperty("versionCode") as? String)?.toIntOrNull()
        val computedVersionCode = envVersionCode ?: propVersionCode ?: ((System.currentTimeMillis() - 1735689600000L) / 60000L).toInt().coerceAtLeast(1)

        versionCode = computedVersionCode
        versionName = "1.0.$computedVersionCode"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose ANILIST_CLIENT_ID from local.properties to BuildConfig
        buildConfigField(
            type = "String",
            name = "ANILIST_CLIENT_ID",
            value = getLocalProperty("ANILIST_CLIENT_ID", project) // No extra quotes needed here
        )
    }

    signingConfigs {
        create("release") {
            val keystoreFile = project.rootProject.file("keystore.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile?.exists() == true) {
                signingConfig = releaseSigning
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {

    //Test and Debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3.expressive)

    //Material Icon
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    //Material 3 Adaptive
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    //Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    //Ktor
    implementation(libs.bundles.ktor)

    //Coil
    implementation(libs.bundles.coil)

    //Chucker
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    //Jetpack Paging
    implementation(libs.bundles.paging)

    //Media3
    implementation(libs.bundles.media3)

    //Browser - Custom Tab Intent
    implementation(libs.androidx.browser)

    //DataStore
    implementation(libs.androidx.datastore)

    //SplashScreen
    implementation(libs.androidx.splashscreen)

    //Material Components
    implementation(libs.material)
}

// Helper function to read property from environment or local.properties
fun getLocalProperty(key: String, project: Project): String {
    val envValue = System.getenv(key)
    if (!envValue.isNullOrBlank()) {
        return "\"${envValue.replace("\"", "")}\""
    }
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        val prop = properties.getProperty(key)
        if (!prop.isNullOrBlank()) {
            return "\"${prop.replace("\"", "")}\""
        }
    }
    return "\"\""
}