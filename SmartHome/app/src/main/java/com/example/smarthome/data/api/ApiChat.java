package com.example.smarthome.data.api;

import com.example.smarthome.BuildConfig;

public class ApiChat {
    public static String API_URL = "https://api.openai.com/v1/chat/completions";
    public static String API_KEY = BuildConfig.API_KEY;
}



// ==================== Ẩn Key +++++++===========================

//Đầu tiên thêm key vào local.properties
//Sau đó vào build.gradle :app để config:
//buildTypes {
//    val properties = Properties()
//    val localPropertiesFile = project.rootProject.file("local.properties")
//
//    if (localPropertiesFile.exists()) {
//        properties.load(FileInputStream(localPropertiesFile))
//    }
//
//    val apiKey = properties.getProperty("API_KEY") ?: "default_api_key"
//
//    debug {
//        buildConfigField("String", "API_KEY", "\"$apiKey\"")
//    }
//
//    release {
//        isMinifyEnabled = false
//        proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//        )
//
//        buildConfigField("String", "API_KEY", "\"$apiKey\"")
//    }