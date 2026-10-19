plugins {
    kotlin("jvm")
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
    testImplementation("junit:junit:4.13.2")
}
