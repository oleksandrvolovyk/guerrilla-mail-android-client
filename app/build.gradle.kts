plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.gradle)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
}

val ADMOB_TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
val ADMOB_TEST_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"

// Load environment variables from .env file
val envFile = rootProject.file(".env")
var useTestIds = true
var errorMessage = ""

// Define the keys we need
val ADMOB_APP_ID_KEY = "ADMOB_APP_ID"
val ADMOB_NATIVE_AD_ID_KEY = "ADMOB_NATIVE_AD_ID"

// Check if the .env file exists
if (envFile.exists()) {
    try {
        // Check if all required keys exist in the .env file
        val missingKeys = mutableListOf<String>()
        if (!env.isPresent(ADMOB_APP_ID_KEY)) missingKeys.add(ADMOB_APP_ID_KEY)
        if (!env.isPresent(ADMOB_NATIVE_AD_ID_KEY)) missingKeys.add(ADMOB_NATIVE_AD_ID_KEY)

        if (missingKeys.isEmpty()) {
            useTestIds = false
        } else {
            errorMessage = "Missing AdMob IDs in .env file: ${missingKeys.joinToString(", ")}"
        }
    } catch (e: Exception) {
        errorMessage = "Error reading .env file: ${e.message}"
    }
} else {
    errorMessage = ".env file not found in project root"
}

android {
    namespace = "volovyk.guerrillamail"
    compileSdk = 35

    defaultConfig {
        applicationId = "volovyk.guerrillamail"
        minSdk = 24
        targetSdk = 34
        versionCode = 42
        versionName = "3.5.4"

        testInstrumentationRunner = "volovyk.MyTestRunner"

        buildConfigField(
            "String",
            "GUERRILLAMAIL_API_BASE_URL",
            "\"https://api.guerrillamail.com/\""
        )
        buildConfigField("String", "MAILTM_API_BASE_URL", "\"https://api.mail.tm/\"")

        // Always use test IDs for debug builds
        buildConfigField("String", "ADMOB_APP_ID", "\"${ADMOB_TEST_APP_ID}\"")
        buildConfigField("String", "ADMOB_NATIVE_AD_ID", "\"${ADMOB_TEST_NATIVE_AD_ID}\"")
        resValue("string", "admob_app_id", ADMOB_TEST_APP_ID)
    }

    buildTypes {
        release {
            if (useTestIds) {
                println("WARNING: RELEASE BUILD IS USING TEST ADMOB IDs. Reason: ${errorMessage}")
            }
            if (!useTestIds) {
                buildConfigField(
                    "String",
                    "ADMOB_APP_ID",
                    "\"${env.fetch(ADMOB_APP_ID_KEY)}\""
                )
                buildConfigField(
                    "String",
                    "ADMOB_NATIVE_AD_ID",
                    "\"${env.fetch(ADMOB_NATIVE_AD_ID_KEY)}\""
                )

                resValue("string", "admob_app_id", env.fetch(ADMOB_APP_ID_KEY))
            }
            isMinifyEnabled = true
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
        buildConfig = true
    }
    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))

    implementation(libs.compose.material3)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    androidTestImplementation(libs.compose.junit4)
    debugImplementation(libs.compose.test.manifest)

    // Navigation
    implementation(libs.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.jackson)
    implementation(libs.jackson)

    // Hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Room Database
    implementation(libs.room)
    ksp(libs.room.compiler)

    // Core libraries
    implementation(libs.core.ktx)

    // Timber
    implementation(libs.timber)

    // LeakCanary
    debugImplementation(libs.leakcanary)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Preferences DataStore
    implementation(libs.datastore.preferences)

    // Ads
    implementation(libs.play.services.ads)
    implementation(project(":nativetemplates"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.core.testing)
    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    testImplementation(libs.retrofit.mock)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.test.runner)
}
