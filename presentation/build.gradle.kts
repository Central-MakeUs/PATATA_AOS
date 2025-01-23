import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").reader())
}

android {
    namespace = "com.cmc.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        resValue("string", "my_web_client_id", properties["MY_WEB_CLIENT_ID"] as String)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(project(":domain"))
    implementation(project(":design"))
    implementation(project(":common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Firebase Auth
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // Glide
    implementation(libs.glide)

    // Paging3
    implementation(libs.androidx.paging.runtime.ktx)

    // ViewPager
    implementation(libs.androidx.viewpager2)
    implementation(libs.dotsindicator)

}