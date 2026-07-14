import com.vanniktech.maven.publish.SonatypeHost
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish") version "0.31.0"
}
group = "com.banxa.nativepaymentssdk"
version = "1.0.0"
android {
    namespace = "com.banxa"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

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
        compose = true
    }

}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(
        "com.banxa.nativepaymentssdk",
        "sdk",
        "1.0.0"
    )

    pom {
        name.set("SDK For Payments")
        description.set("Android SDK for payments")
        url.set("https://github.com/BanxaOfficial/android-payments-sdk")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("<MavenUserID>")
                name.set("Banxa PaymentSDK")
                email.set("<MavenUserEmail>")
            }
        }

        scm {
            url.set("https://github.com/BanxaOfficial/android-payments-sdk")
            connection.set("scm:git:git://github.com/BanxaOfficial/android-payments-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/BanxaOfficial/android-payments-sdk.git")
        }
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("io.primer:checkout:3.0.0-beta.2")
    implementation("io.primer:android:3.0.0-beta.2")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    api("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.17")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}