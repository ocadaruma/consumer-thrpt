plugins {
    id("java")
}

group = "com.mayreh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.9.0")
    implementation("org.apache.kafka:kafka_2.13:3.9.0")
    implementation("org.apache.kafka:kafka-clients:3.9.0:test")
    implementation("org.apache.kafka:kafka_2.13:3.9.0:test")
    implementation("org.apache.kafka:kafka-server-common:3.9.0:test")
    implementation(platform("org.junit:junit-bom:5.10.0"))
    implementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
