import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").reader())
}

android {
    namespace = "com.cmc.patata"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cmc.patata"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "naver_map_client_id", properties["NAVER_MAP_CLIENT_ID"] as String)
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file(properties["DEBUG_KEY_STORE_PATH"] as String)
            storePassword = properties["DEBUG_STORE_PASSWORD"] as String
            keyAlias = properties["DEBUG_KEY_ALIAS"] as String
            keyPassword = properties["DEBUG_KEY_PASSWORD"] as String
        }

        create("release") {
            storeFile = file(properties["RELEASE_KEY_STORE_PATH"] as String)
            storePassword = properties["RELEASE_STORE_PASSWORD"] as String
            keyAlias = properties["RELEASE_KEY_ALIAS"] as String
            keyPassword = properties["RELEASE_KEY_PASSWORD"] as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dataBinding {
        enable = true
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {

    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":common"))
    implementation(project(":design"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // OkHttp (Logging Interceptor)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Naver
    implementation(libs.map.sdk)

}