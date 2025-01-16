plugins {
    id("crud-test.kotlin-conventions")
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass = "ca.dialai.crud.test.MainKt"
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    runtimeOnly(libs.ktor.client.okhttp)
}