import com.android.build.api.dsl.androidLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.serialization)

    kotlin("native.cocoapods")

}

kotlin {

    cocoapods {
        summary = "OneSignal KMP wrapper"
        homepage = "https://github.com/mamon-aburawi/onesignal-kmp"
        version = "1.0.0"
        ios.deploymentTarget = "12.0" // OneSignal minimum requirement

        pod("OneSignalXCFramework") {
            version = "5.5.0" // Use the latest OneSignal major version
        }
    }



    jvm()

    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "io.geolocation.kmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_21
                )
            }
        }
    }

    js{
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {


            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)


            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

            implementation(libs.bundles.ktor)

        }



        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)


            implementation(libs.onesignal)

        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }


        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.cio)



        }

        webMain.dependencies {

        }


    }
}


group = "io.github.mamon-aburawi" // this group name in maven central repository
version = "1.0.0" // version of library

mavenPublishing {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
            androidVariantsToPublish = listOf("release", "debug"),
        )
    )


    coordinates(
        groupId = group.toString(),
        version = version.toString(),
        artifactId = "onesignal-kmp"
    )

    pom {
        name = "Onesignal KMP"
        description = ""
        inceptionYear = "2026"
        url = ""
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                name = "Mamon Aburawi"
                email = "mamon.aburawi@gmail.com"
            }
        }
        scm {
            url = ""
        }
    }

    publishToMavenCentral()

    signAllPublications()
}