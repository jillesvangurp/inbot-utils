buildscript {
    repositories {
        mavenCentral()
    }
}
repositories {
    mavenCentral()
//    maven("https://maven.tryformation.com/releases") {
//        content {
//            includeGroup("com.jillesvangurp")
//        }
//    }
}

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

dependencies {
    api(Kotlin.stdlib.jdk8)

    implementation("org.bouncycastle:bcprov-jdk15on:_")
    implementation("com.auth0:java-jwt:_")
    implementation("org.apache.commons:commons-lang3:_")
    implementation("commons-codec:commons-codec:_")
    implementation("com.google.guava:guava:_")
    implementation("org.slf4j:slf4j-api:_")
    testImplementation("org.testng:testng:_")
    testImplementation("org.assertj:assertj-core:_")
}

tasks.withType<Test> {
    useTestNG()
//    useJUnitPlatform()
//    testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//    testLogging.events = setOf(
//        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
//        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
//        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
//        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
//        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
//    )
}

val artifactName = "inbot-utils"
val artifactGroup = "com.github.jillesvangurp"

val sourceJar = task("sourceJar", Jar::class) {
    dependsOn(tasks["classes"])
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar = task("javadocJar", Jar::class) {
    from(tasks["dokkaJavadoc"])
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = artifactGroup
            artifactId = artifactName
            pom {
                description.set("Kts extensions for kt-search. Easily script operations for Elasticsearch and Opensearch with .main.kts scripts")
                name.set(artifactId)
                url.set("https://github.com/jillesvangurp/inbot-utils")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/jillesvangurp/inbot-utils/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("jillesvangurp")
                        name.set("Jilles van Gurp")
                    }
                }
                scm {
                    url.set("https://github.com/jillesvangurp/inbot-utils/LICENSE")
                }
            }

            from(components["java"])
            artifact(sourceJar)
            artifact(javadocJar)
        }
    }
}
