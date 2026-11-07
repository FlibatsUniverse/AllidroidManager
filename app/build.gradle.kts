import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.flibatsuniverse.allidroid.manager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.flibatsuniverse.allidroid.manager"
        minSdk = 28
        targetSdk = 34
        versionCode = 1123
        versionName = "0.1.0"

        buildConfigField("String", "MOD_NAME", "\"Allidroid\"")
        buildConfigField("String", "MANAGER_NAME", "\"AllidroidManager\"")
        buildConfigField("String", "REPO", "\"FlibatsUniverse/Allidroid\"")
        buildConfigField("String", "ORG_LINK", "\"https://github.com/FlibatsUniverse\"")
        buildConfigField("String", "INVITE_LINK", "\"https://discord.com/invite/XszKXdqaZP\"")
        buildConfigField("String", "WEBSITE_LINK", "\"https://alliance.flibat.com\"")
        buildConfigField("String", "REPO_LINK", "\"https://github.com/FlibatsUniverse/AllidroidManager\"")
        buildConfigField("String", "MODDED_APP_PACKAGE_NAME", "\"com.flibatsuniverse.allidroid\"")
        buildConfigField("int", "MODDED_APP_ICON", "0xFFCAA7F5")
        buildConfigField("int", "MODDED_APP_ICON_ALPHA", "0xFFB889E8")
        buildConfigField("int", "MODDED_APP_ICON_OTHER", "0xFF9A6BD0")

        buildConfigField("String", "GIT_BRANCH", "\"${getCurrentBranch()}\"")
        buildConfigField("String", "GIT_COMMIT", "\"${getLatestCommit()}\"")
        buildConfigField("boolean", "GIT_LOCAL_COMMITS", "${hasLocalCommits()}")
        buildConfigField("boolean", "GIT_LOCAL_CHANGES", "${hasLocalChanges()}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        named("release") {
            isCrunchPngs = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    androidComponents {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.apply {
                // Debug metadata
                add("/**/*.version")
                add("/kotlin-tooling-metadata.json")
                // Kotlin debugging (https://github.com/Kotlin/kotlinx.coroutines/issues/2274)
                add("/DebugProbesKt.bin")
            }
        }
    }

    packaging {
        resources {
            // Reflection symbol list (https://stackoverflow.com/a/41073782/13964629)
            excludes += "/**/*.kotlin_builtins"
        }
    }

    configurations {
        all {
            exclude(module = "listenablefuture")
            exclude(module = "error_prone_annotations")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    implementation(platform(libs.compose.bom))

    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.shizuku)
    implementation(libs.bundles.voyager)

    implementation(files("libs/lspatch.aar"))
    implementation(libs.aboutlibraries.core)
    implementation(libs.binaryResources) {
        exclude(module = "checker-qual")
        exclude(module = "jsr305")
        exclude(module = "guava")
    }
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections)
    implementation(libs.zip.android) {
        artifact {
            type = "aar"
        }
    }
}

fun getCurrentBranch(): String? =
    exec("git", "symbolic-ref", "--short", "HEAD")

fun getLatestCommit(): String? =
    exec("git", "rev-parse", "--short", "HEAD")

fun hasLocalCommits(): Boolean {
    val branch = getCurrentBranch() ?: return false
    return exec("git", "log", "origin/$branch..HEAD")?.isNotBlank() ?: false
}

fun hasLocalChanges(): Boolean =
    exec("git", "status", "-s")?.isNotEmpty() ?: false

fun exec(vararg command: String): String? {
    return try {
        val stdout = ByteArrayOutputStream()
        val errout = ByteArrayOutputStream()

        exec {
            commandLine = command.toList()
            standardOutput = stdout
            errorOutput = errout
            isIgnoreExitValue = true
        }

        if(errout.size() > 0)
            throw Error(errout.toString(Charsets.UTF_8))

        stdout.toString(Charsets.UTF_8).trim()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}