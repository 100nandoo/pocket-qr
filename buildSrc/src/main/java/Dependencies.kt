object Misc {
    const val appId = "com.hapley.pocketqr"
    const val compileSdk = 31
    const val minSdk = 24
    const val targetSdk = 31
}

object Modules {
    const val app = ":app"
    var core = ":core"
    const val preview = ":preview"
}

object Releases {
    const val versionCode = 20
    const val versionName = "0.9.0"
}

object Versions {
    const val kotlin = "1.6.10"
    const val coroutinePlayServices = "1.6.0"
    const val coroutineGuava = "1.6.0"
    const val gradle = "7.1.2"
    const val gms = "4.3.10"
    const val crashlyticsGradle = "2.8.1"
    const val perfPlugin = "1.4.1"
    const val ktlint = "0.4.0"

    object Android {
        const val appCompat = "1.4.1"
        const val browser = "1.4.0"
        const val cameraX = "1.0.2"
        const val cameraView = "1.0.0-alpha28"
        const val constraintLayout = "2.1.3"
        const val core = "1.7.0"
        const val hilt = "2.40"
        const val lifecycle = "2.4.1"
        const val material = "1.4.0"
        const val navigation = "2.4.1"
        const val playCore = "1.10.3"
        const val playCoreKtx = "1.8.1"
        const val preference = "1.2.0"
        const val room = "2.3.0"
    }

    object Firebase {
        const val bom = "29.1.0"
        const val qrCode = "17.0.0"
    }

    const val appIntro = "6.1.0"

    //    const val assent = "3.0.0-RC4"
    const val coil = "1.4.0"
    const val epoxy = "4.6.3"

    const val khronos = "0.9.0"


    const val lottie = "4.1.0"

    const val rvDivider = "3.5.0"
    const val timber = "5.0.1"
    const val zxingAndroid = "4.3.0"

    // Tests
    const val espresso = "3.4.0"
    const val junit = "4.13.2"
    const val junitExt = "1.1.3"

    // Debug
    const val leakCanary = "2.8.1"
}

object Libraries {
    const val appIntro = "com.github.AppIntro:AppIntro:${Versions.appIntro}"
//    const val assent = "com.afollestad.assent:core:${Versions.assent}"

    const val coil = "io.coil-kt:coil:${Versions.coil}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyKapt = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"

    const val khronos = "com.github.hotchemi:khronos:${Versions.khronos}"

    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    const val rvDivider = "com.github.fondesa:recycler-view-divider:${Versions.rvDivider}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val zxingAndroid = "com.journeyapps:zxing-android-embedded:${Versions.zxingAndroid}"
}

object TestLibraries {
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val junit = "junit:junit:${Versions.junit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
}

object DebugLibraries {
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}

object KotlinLibraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val coroutinePlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutinePlayServices}"
    const val coroutineGuava = "org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.coroutineGuava}"
}

object AndroidLibraries {
    // ANDROID
    const val appCompat = "androidx.appcompat:appcompat:${Versions.Android.appCompat}"

    const val browser = "androidx.browser:browser:${Versions.Android.browser}"

    const val camera2 = "androidx.camera:camera-camera2:${Versions.Android.cameraX}"
    const val cameraLifecycle = "androidx.camera:camera-lifecycle:${Versions.Android.cameraX}"
    const val cameraView = "androidx.camera:camera-view:${Versions.Android.cameraView}"

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.Android.constraintLayout}"
    const val core = "androidx.core:core-ktx:${Versions.Android.core}"

    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.Android.hilt}"
    const val hiltKapt = "com.google.dagger:hilt-compiler:${Versions.Android.hilt}"

    const val material = "com.google.android.material:material:${Versions.Android.material}"

    const val lifecycleLiveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Android.lifecycle}"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Android.lifecycle}"

    const val navigationFrag = "androidx.navigation:navigation-fragment-ktx:${Versions.Android.navigation}"
    const val navigation = "androidx.navigation:navigation-ui-ktx:${Versions.Android.navigation}"

    const val playCore = "com.google.android.play:core:${Versions.Android.playCore}"
    const val playCoreKtx = "com.google.android.play:core-ktx:${Versions.Android.playCoreKtx}"
    const val preference = "androidx.preference:preference-ktx:${Versions.Android.preference}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.Android.room}"
    const val room = "androidx.room:room-ktx:${Versions.Android.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.Android.room}"
}

object FirebaseLibraries {
    const val bom = "com.google.firebase:firebase-bom:${Versions.Firebase.bom}"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val indexing = "com.google.firebase:firebase-appindexing"
    const val performance = "com.google.firebase:firebase-perf-ktx"
    const val qrCode = "com.google.mlkit:barcode-scanning:${Versions.Firebase.qrCode}"
}

object ProjectLibraries {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Android.hilt}"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Android.navigation}"
    const val gms = "com.google.gms:google-services:${Versions.gms}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradle}"
    const val perfPlugin = "com.google.firebase:perf-plugin:${Versions.perfPlugin}"
}