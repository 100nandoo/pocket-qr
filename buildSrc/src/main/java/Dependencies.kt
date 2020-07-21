object Misc {
    val appId = "com.hapley.pocketqr"
    val compileSdk = 29
    val minSdk = 21
    val targetSdk = 29
    val buildTools = "29.0.3"
}

object ProductFlavors {
    val dimensions = "default"
    val freeSuffix = "-free"
    val proSuffix = "-pro"
}

object Releases {
    val versionCode = 1
    val versionName = "0.3"
}

object Versions {
    val kotlin = "1.3.72"
    val coroutinePlayServices = "1.3.7"
    val coroutineGuava = "1.3.7"
    val gradle = "4.0.0"
    val gms = "4.3.3"

    object Android {
        val appCompat = "1.1.0"
        val ads = "19.2.0"
        val cameraX = "1.0.0-beta06"
        val cameraView = "1.0.0-alpha13"
        val constraintLayout = "1.1.3"
        val core = "1.2.0"
        val lifecycle = "2.2.0"
        val material = "1.2.0-alpha06"
        val navigation = "2.3.0"
        val preference = "1.1.1"
        val room = "2.2.5"
    }

    object Firebase {
        val core = "17.4.2"
        val qrCode = "16.0.0"
    }

    val assent = "3.0.0-RC4"
    val cameraView = "2.6.2"
    val fancyShowcaseView = "1.3.0"
    val fastAdapter = "5.2.0"
    val koin = "2.1.6"
    val materialValues = "1.1.1"
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
    val assent = "com.afollestad.assent:core:${Versions.assent}"
    val cameraView = "com.otaliastudios:cameraview:${Versions.cameraView}"
    val fancyShowcaseView = "me.toptas.fancyshowcase:fancyshowcaseview:${Versions.fancyShowcaseView}"

    val fastAdapter = "com.mikepenz:fastadapter:${Versions.fastAdapter}"
    val fastAdapterDiff = "com.mikepenz:fastadapter-extensions-diff:${Versions.fastAdapter}"
    val fastAdapterUi = "com.mikepenz:fastadapter-extensions-ui:${Versions.fastAdapter}"
    val fastAdapterUtils = "com.mikepenz:fastadapter-extensions-utils:${Versions.fastAdapter}"

    val koin = "org.koin:koin-androidx-scope:${Versions.koin}"
    val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    val materialValues = "blue.aodev:material-values:${Versions.materialValues}"

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
    val ads = "com.google.android.gms:play-services-ads:${Versions.Android.ads}"

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
    val core = "com.google.firebase:firebase-core:${Versions.Firebase.core}"
    val analytics = "com.google.firebase:firebase-analytics:${Versions.Firebase.core}"

    val qrCode = "com.google.mlkit:barcode-scanning:${Versions.Firebase.qrCode}"
}

object ProjectLibraries {
    val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Android.navigation}"
    val gms = "com.google.gms:google-services:${Versions.gms}"
}