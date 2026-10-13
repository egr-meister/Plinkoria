plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// ---- Release signing: env vars only, never a debug-key fallback ----
val keystorePath: String? = System.getenv("ANDROID_KEYSTORE_PATH")?.takeIf { it.isNotBlank() }
val keystorePassword: String? = System.getenv("ANDROID_KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }
val keyAliasEnv: String? = System.getenv("ANDROID_KEY_ALIAS")?.takeIf { it.isNotBlank() }
val keyPasswordEnv: String? = System.getenv("ANDROID_KEY_PASSWORD")?.takeIf { it.isNotBlank() }
val releaseSigningAvailable = listOf(keystorePath, keystorePassword, keyAliasEnv, keyPasswordEnv).all { it != null }

android {
    namespace = "com.plinkoria.game"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.plinkoria.game"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            if (releaseSigningAvailable) {
                storeFile = file(keystorePath!!)
                storePassword = keystorePassword
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
                storeType = "PKCS12"
            }
        }
    }

    buildTypes {
        release {
            // Staged R8: ship non-minified first; enable both after a verified release (see README).
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Fail loudly if any release artifact is requested without signing env vars.
val releaseArtifactTasks = setOf(
    "assembleRelease", "bundleRelease", "packageRelease",
    "packageReleaseBundle", "signReleaseBundle", "installRelease"
)
gradle.taskGraph.whenReady {
    val wantsRelease = allTasks.any { it.project == project && it.name in releaseArtifactTasks }
    if (wantsRelease && !releaseSigningAvailable) {
        throw GradleException(
            "Release signing is not configured. Set ANDROID_KEYSTORE_PATH, ANDROID_KEYSTORE_PASSWORD, " +
                "ANDROID_KEY_ALIAS and ANDROID_KEY_PASSWORD (PKCS12 keystore). " +
                "Refusing to build a release artifact — debug signing is never used for release."
        )
    }
    if (wantsRelease && keystorePath != null && !file(keystorePath).exists()) {
        throw GradleException("ANDROID_KEYSTORE_PATH points to a missing file: $keystorePath")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.serialization.json)
}
