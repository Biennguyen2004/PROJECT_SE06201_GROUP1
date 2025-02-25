plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smarthome"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smarthome"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ==================thư viện cho phần AI=============
    // Volley Library
    implementation("com.android.volley:volley:1.2.1")
    // ok http
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // ================================


    // ===============Thư viện cho Call api retrofit========
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //================Websocket=======================
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Thư viện inLogcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

}