object Misc {
    val appId = "com.hapley.pocketqr"
    val compileSdk = 30
    val minSdk = 21
    val targetSdk = 30
    val buildTools = "30.0.1"
}

object ProductFlavors {
    val dimensions = "default"
    val freeSuffix = "-free"
    val proSuffix = "-pro"
}

object Releases {
    val versionCode = 7
    val versionName = "0.8.2"
}

object Versions {
    val kotlin = "1.4.10"
    val coroutinePlayServices = "1.3.9"
    val coroutineGuava = "1.3.9"
    val gradle = "4.0.1"
    val gms = "4.3.3"
    val crashlyticsGradle = "2.3.0"
    val perfPlugin = "1.3.1"

    object Android {
        val appCompat = "1.2.0"
        val browser = "1.2.0"
        val cameraX = "1.0.0-beta09"
        val cameraView = "1.0.0-alpha16"
        val constraintLayout = "2.0.0"
        val core = "1.3.1"
        val lifecycle = "2.2.0"
        val material = "1.2.0"
        val navigation = "2.3.0"
        val preference = "1.1.1"
        val room = "2.2.5"
    }

    object Firebase {
        val ads = "19.4.0"
        val core = "17.5.0"
        val crashlytics = "17.2.1"
        val indexing = "19.1.0"
        val performance = "19.0.8"
        val qrCode = "16.0.3"
    }

    val appIntro = "6.0.0"

    //    val assent = "3.0.0-RC4"
    val coil = "1.0.0-rc1"
    val fastAdapter = "5.2.3"

    val khronos = "0.9.0"
    val koin = "2.1.6"

    val koptional = "1.7.0"

    val lottie = "3.4.2"

    val materialValues = "1.1.1"

    val awesomeQr = "1.2.0"
    val rvDivider = "3.3.0"
    val timber = "4.7.1"

    // Tests
    val espresso = "3.2.0"
    val junit = "4.13"
    val junitExt = "1.1.1"

    // Debug
    val leakCanary = "2.4"
}

object Libraries {
    val appIntro = "com.github.AppIntro:AppIntro:${Versions.appIntro}"
//    val assent = "com.afollestad.assent:core:${Versions.assent}"

    val coil = "io.coil-kt:coil:${Versions.coil}"

    val fastAdapter = "com.mikepenz:fastadapter:${Versions.fastAdapter}"
    val fastAdapterDiff = "com.mikepenz:fastadapter-extensions-diff:${Versions.fastAdapter}"
    val fastAdapterUi = "com.mikepenz:fastadapter-extensions-ui:${Versions.fastAdapter}"
    val fastAdapterUtils = "com.mikepenz:fastadapter-extensions-utils:${Versions.fastAdapter}"

    val khronos = "com.github.hotchemi:khronos:${Versions.khronos}"

    val koin = "org.koin:koin-androidx-scope:${Versions.koin}"
    val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    val koptional = "com.gojuno.koptional:koptional:${Versions.koptional}"

    val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    val materialValues = "blue.aodev:material-values:${Versions.materialValues}"

    val awesomeQr = "com.github.SumiMakito:AwesomeQRCode:${Versions.awesomeQr}"
    val rvDivider = "com.github.fondesa:recycler-view-divider:${Versions.rvDivider}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
}

object TestLibraries {
    val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    val junit = "junit:junit:${Versions.junit}"
    val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
}

object DebugLibraries {
    val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}

object KotlinLibraries {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val coroutinePlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutinePlayServices}"
    val coroutineGuava = "org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.coroutineGuava}"
}

object AndroidLibraries {
    // ANDROID
    val appCompat = "androidx.appcompat:appcompat:${Versions.Android.appCompat}"

    val browser = "androidx.browser:browser:${Versions.Android.browser}"

    val camera2 = "androidx.camera:camera-camera2:${Versions.Android.cameraX}"
    val cameraLifecycle = "androidx.camera:camera-lifecycle:${Versions.Android.cameraX}"
    val cameraView = "androidx.camera:camera-view:${Versions.Android.cameraView}"

    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.Android.constraintLayout}"
    val core = "androidx.core:core-ktx:${Versions.Android.core}"
    val material = "com.google.android.material:material:${Versions.Android.material}"

    val lifecycleExt = "androidx.lifecycle:lifecycle-extensions:${Versions.Android.lifecycle}"
    val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Android.lifecycle}"

    val navigationFrag = "androidx.navigation:navigation-fragment-ktx:${Versions.Android.navigation}"
    val navigation = "androidx.navigation:navigation-ui-ktx:${Versions.Android.navigation}"

    val preference = "androidx.preference:preference-ktx:${Versions.Android.preference}"

    val roomRuntime = "androidx.room:room-runtime:${Versions.Android.room}"
    val room = "androidx.room:room-ktx:${Versions.Android.room}"
    val roomCompiler = "androidx.room:room-compiler:${Versions.Android.room}"
}

object FirebaseLibraries {
    val ads = "com.google.android.gms:play-services-ads:${Versions.Firebase.ads}"
    val analytics = "com.google.firebase:firebase-analytics-ktx:${Versions.Firebase.core}"
    val core = "com.google.firebase:firebase-core:${Versions.Firebase.core}"
    val crashlytics = "com.google.firebase:firebase-crashlytics:${Versions.Firebase.crashlytics}"
    val indexing = "com.google.firebase:firebase-appindexing:${Versions.Firebase.indexing}"
    val performance = "com.google.firebase:firebase-perf:${Versions.Firebase.performance}"
    val qrCode = "com.google.mlkit:barcode-scanning:${Versions.Firebase.qrCode}"
}

object ProjectLibraries {
    val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Android.navigation}"
    val gms = "com.google.gms:google-services:${Versions.gms}"
    val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradle}"
    val perfPlugin = "com.google.firebase:perf-plugin:${Versions.perfPlugin}"
}