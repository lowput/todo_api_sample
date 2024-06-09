plugins {
    alias(libs.plugins.ktor)
    application
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("app.cash.sqldelight") version "2.0.0-alpha04"
}

group = "jp.lowput.todo_api_sample.staging"
version = "1.0.0"

kotlin {
    jvm() {
        withJava()
        this.compilations.all {
            application.mainClass.set("jp.lowput.todo_api_sample.staging.Application_jvmKt")
            application.applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
        }
    }


    // linuxArm64 or linuxX64 or macosArm64 or macosX64 どれかを指定する。
    // :shared、:server 両方変える
    // クロスコンパイルできない
    // Docker-Linux-aarch64 ではビルドできない linux版のlibsqlite3,libpqさえ拾ってくれば…M2-MACでできるかも
    // libsqlite3,libpqのインストールが必要
    val hostOs = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val nativeTarget = when {
        hostOs == "Mac OS X" && arch == "x86_64"  -> macosX64("native")
        hostOs == "Mac OS X" && arch == "aarch64" -> macosArm64("native")
        hostOs == "Linux"    && arch == "x86_64"  -> linuxX64("native")
        hostOs == "Linux"    && arch == "aarch64" -> null
        else -> null
    }
    nativeTarget?.apply {
        binaries {
            executable {
                entryPoint = "jp.lowput.todo_api_sample.staging.main"
            }
        }
    }

    sourceSets.jvmMain {
        kotlin.srcDirs("src/jvmMain/kotlin")
        dependencies {
            implementation(kotlin("stdlib"))
            implementation(projects.shared)
            implementation(libs.logback)
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
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.postgresql)
            implementation(libs.jdbi3.sqlobject)
            implementation(libs.jdbi3.kotlin)

            implementation(libs.ktor.server.content.negotiation)
        }
    }
    sourceSets.nativeMain.dependencies {
        implementation(libs.postgres.native.sqldelight.driver)
    }
}