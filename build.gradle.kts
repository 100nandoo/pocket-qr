// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(ProjectLibraries.gradle)
        classpath(ProjectLibraries.kotlinGradle)
        classpath(ProjectLibraries.navigationSafeArgs)
        classpath(ProjectLibraries.gms)
        classpath(ProjectLibraries.crashlytics)
        classpath(ProjectLibraries.perfPlugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}