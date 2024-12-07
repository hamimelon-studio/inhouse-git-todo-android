import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mikeapp.newideatodoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mikeapp.newideatodoapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        val localPropertiesFile = File(project.rootDir, "local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())  // Load properties into Properties object
        }
        val staticApiToken = localProperties.getProperty("staticApi.token", "ci")
        buildConfigField("String", "STATIC_API_TOKEN", "\"$staticApiToken\"")

        val googleMapApiKey = localProperties.getProperty("googleMapApiKey", "")
        buildConfigField("String", "googleMapApiKey", "\"$googleMapApiKey\"")
        resValue("string", "googleMapApiKey", googleMapApiKey ?: "")

        buildConfigField("String", "testLocationHomeLat",
            "\"${localProperties.getProperty("testLocation.home.lat", "-33.8688")}\"")
        buildConfigField("String", "testLocationHomeLon",
            "\"${localProperties.getProperty("testLocation.home.lon", "151.2093")}\"")
        buildConfigField("String", "testLocationWorkLat",
            "\"${localProperties.getProperty("testLocation.home.lat", "-33.8688")}\"")
        buildConfigField("String", "testLocationWorkLon",
            "\"${localProperties.getProperty("testLocation.home.lon", "151.2093")}\"")
    }

    signingConfigs {
        getByName("debug") {
            val localProperties = Properties()
            val localPropertiesFile = File(project.rootDir, "local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(localPropertiesFile.inputStream())  // Load properties into Properties object
            }
            storeFile = File(localProperties.getProperty("DEBUG_KEYSTORE_FILE", "/path/to/debug.keystore"))
            storePassword = localProperties.getProperty("DEBUG_KEYSTORE_PASSWORD", "")
            keyAlias = localProperties.getProperty("DEBUG_KEY_ALIAS", "")
            keyPassword = localProperties.getProperty("DEBUG_KEY_PASSWORD", "")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // network
    implementation(libs.retrofit.v290)
    implementation(libs.converter.gson.v290)
    implementation(libs.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // geo
    implementation(libs.gms.play.services.location)

    // map
    implementation(libs.play.services.maps)
    implementation(libs.gms.play.services.location)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.places)

    // di
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}