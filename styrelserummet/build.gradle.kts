plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.test {
    useJUnit()
}

// GitHub Actions kör testDebugUnitTest på alla moduler; ren JVM-modul har bara test.
tasks.register("testDebugUnitTest") {
    dependsOn(tasks.test)
}

dependencies {
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation("junit:junit:4.13.2")
}
