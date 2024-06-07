import org.jetbrains.kotlin.cli.jvm.main

plugins {
    alias(libs.plugins.ktor)
    application
    alias(libs.plugins.kotlinMultiplatform)
}

group = "jp.classi.portal.staging"
version = "1.0.0"

kotlin {
    jvm() {
        withJava()
        this.compilations.all {
            application.mainClass.set("jp.classi.portal.staging.Application_jvmKt")
            application.applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
        }
    }
    linuxArm64("native") {
        binaries {
            executable {
                entryPoint = "jp.classi.portal.staging.main"
            }
        }
    }

    sourceSets.jvmMain {
        kotlin.srcDirs("src/jvmMain/kotlin")
        dependencies {
            implementation(kotlin("stdlib"))
            implementation(projects.shared)
            implementation(libs.logback)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
        }
    }
    sourceSets.nativeMain {
        dependencies {
        }
    }
    sourceSets.jvmTest.dependencies {
        implementation(libs.ktor.server.tests)
        implementation(libs.kotlin.test.junit)
    }
    sourceSets.commonMain {
        kotlin.srcDirs("src/commonMain/kotlin")
        dependencies {
            implementation(projects.shared)
            implementation(libs.logback)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
        }
    }
}