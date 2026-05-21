import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlin.multiplatform)
}

group = "net.lsafer.bcrypt"

kotlin {
    jvm()
    js { nodejs() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { nodejs() }
    sourceSets {
        val commonMain by getting
        val jsMain by getting
        val wasmJsMain by getting

        val webCommon by creating
        webCommon.dependsOn(commonMain)
        jsMain.dependsOn(webCommon)
        wasmJsMain.dependsOn(webCommon)
    }
    sourceSets.commonMain.dependencies {
        implementation(kotlin("stdlib"))
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotlinx.coroutines.test)
    }
    sourceSets.jvmMain.dependencies {
        implementation("at.favre.lib:bcrypt:0.10.2")
    }
    sourceSets.named("webCommon").dependencies {
        implementation(libs.kotlinx.browser)
        implementation(npm("bcryptjs", "^3.0.2"))
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    if (project.properties["doSign"] == "yes")
        signAllPublications()
    coordinates(
        groupId = group.toString(),
        artifactId = "bcrypt",
        version = version.toString(),
    )
    pom {
        name = "BCrypt"
        description = "Multiplatform bcrypt library"
        inceptionYear = "2025"
        url = "https://github.com/LSafer/bcrypt/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "LSafer"
                name = "Sulaiman Oboody"
                url = "https://github.com/LSafer/"
            }
        }
        scm {
            url = "https://github.com/LSafer/bcrypt/"
            connection = "scm:git:git://github.com/LSafer/bcrypt.git"
            developerConnection = "scm:git:ssh://git@github.com/LSafer/bcrypt.git"
        }
    }
}
