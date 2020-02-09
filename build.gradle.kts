plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"

    // Apply the application plugin to add support for building a CLI application.
    application
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
        java.srcDir(file("src/integration-test/kotlin"))
        resources.srcDir(file("src/integration-test/resources"))
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())


val kafka = "2.2.1"
val junit = "5.1.0"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Kafka deps
    compile("org.apache.kafka:kafka-streams:$kafka")

    // config
    compile("com.sksamuel.hoplite:hoplite-core:1.1.4")
    compile("com.sksamuel.hoplite:hoplite-yaml:1.1.4")

    // Tests
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test")
    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
    integrationTestImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    integrationTestImplementation("org.apache.kafka:kafka-streams-test-utils:$kafka")
}

application {
    // Define the main class for the application
    mainClassName = "com.hello.kafka.start.AppKt"
}
