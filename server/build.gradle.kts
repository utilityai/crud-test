plugins {
    id("crud-test.kotlin-conventions")
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass = "ca.dialai.crud.test.MainKt"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}