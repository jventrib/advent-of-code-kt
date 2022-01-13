import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.6.10"
}

repositories {
    mavenCentral()
}

kotlin {
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native")
//    }
//
//    nativeTarget.apply {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }

    macosX64()

    jvm {
        withJava()
        jvm {
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }
    }


    sourceSets {
        val macosX64Main by getting
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }


}


//tasks {
//    test {
//        useJUnitPlatform()
////        setExcludes(listOf("**"))
//    }
//
//    wrapper {
//        gradleVersion = "7.3"
//    }
//
//    dependencies {
//        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
//        implementation("com.github.ajalt.mordant:mordant:2.0.0-beta4")
//        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
//        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
//    }
//}

//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    freeCompilerArgs = listOf(
//        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
//        "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
//    )
//
//}
