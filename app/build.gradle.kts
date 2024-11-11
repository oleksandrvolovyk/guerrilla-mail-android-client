plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.gradle)
    alias(libs.plugins.room)
}

android {
    namespace = "volovyk.guerrillamail"
    compileSdk = 34

    defaultConfig {
        applicationId = "volovyk.guerrillamail"
        minSdk = 24
        targetSdk = 34
        versionCode = 40
        versionName = "3.5.2"

        testInstrumentationRunner = "volovyk.MyTestRunner"

        buildConfigField(
            "String",
            "GUERRILLAMAIL_API_BASE_URL",
            "\"https://api.guerrillamail.com/\""
        )
        buildConfigField("String", "MAILTM_API_BASE_URL", "\"https://api.mail.tm/\"")
    }

    buildTypes {
        release {
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
