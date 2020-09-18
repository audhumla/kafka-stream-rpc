plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    jcenter()
    mavenCentral()
    maven {
        // confluent
        url = uri("https://packages.confluent.io/maven/")
    }
}

sourceSets {
    create("integrationTest") {
        java.srcDir(file("src/integrationTest/kotlin"))
        resources.srcDir(file("src/integrationTest/resources"))
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())


val kafka = "2.5.0"
val junit = "5.6.2"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Kafka deps
    implementation("org.apache.kafka:kafka-streams:$kafka")

    // config
    implementation("com.sksamuel.hoplite:hoplite-core:1.1.4")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.1.4")

    // Tests
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testImplementation("org.assertj:assertj-core:3.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test")
    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    integrationTestImplementation("org.apache.kafka:kafka-streams-test-utils:$kafka")
    integrationTestImplementation("org.assertj:assertj-core:3.6.2")
}
