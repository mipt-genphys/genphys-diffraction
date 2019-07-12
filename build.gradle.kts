import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles

plugins {
    kotlin("multiplatform") version "1.3.41"
    application
}

application{
    mainClassName = "ru.mipt.phys.diffraction.AppFrameKt"
}

repositories {
    jcenter()
}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js {
        compilations.all {
            kotlinOptions {
                sourceMap = true
                sourceMapEmbedSources = "always"
                moduleKind = "umd"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.3.0-M2")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.register<JavaExec>("runSwing") {
    group = "run"
    val target = kotlin.targets["jvm"]
    val compilation: KotlinCompilationToRunnableFiles<KotlinCommonOptions> =
        target.compilations["main"] as KotlinCompilationToRunnableFiles<KotlinCommonOptions>

    val classes = files(
        compilation.runtimeDependencyFiles,
        compilation.output.allOutputs
    )
    classpath = classes


    main = "ru.mipt.phys.diffraction.AppFrameKt"
}

defaultTasks("runSwing")
