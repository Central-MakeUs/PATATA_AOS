import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
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
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Naver Map
    implementation(libs.map.sdk)
}