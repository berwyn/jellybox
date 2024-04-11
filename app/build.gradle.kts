plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)

    alias(libs.plugins.molecule)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "dev.berwyn.jellybox"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.berwyn.jellybox"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

sqldelight {
    databases {
        create("Jellybox") {
            packageName.set("dev.berwyn.jellybox.data.local")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    ksp(libs.lyricist.processor)

    implementation(platform(libs.bom.compose))
    androidTestImplementation(platform(libs.bom.compose))

    implementation(platform(libs.bom.okhttp))

    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("androidx.tracing:tracing-ktx:1.2.0")

    implementation(libs.media3.datasource.okhttp)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)
    implementation(libs.media3.extractor)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.exoplayer.rtsp)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha06")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha03")
    implementation("androidx.compose.runtime:runtime-tracing:1.0.0-beta01")

    implementation("org.jellyfin.sdk:jellyfin-core:1.4.6")

    implementation(libs.voyager.navigator)
    implementation(libs.voyager.bottomSheetNavigator)
    implementation(libs.voyager.tabNavigator)
    implementation(libs.voyager.screenModel)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.koin)

    implementation(libs.lyricist)


    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.flowlayout)

    implementation(libs.store5)
    implementation(libs.atomicfu)
    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)

    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.palette)
    implementation(libs.landscapist.placeholder)
    implementation(libs.orbital)

    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.lifecycle:lifecycle-runtime-testing:2.7.0")
    testImplementation("io.insert-koin:koin-test:3.4.1")
    testImplementation("io.insert-koin:koin-test-junit4:3.4.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("io.insert-koin:koin-test:3.4.1")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
