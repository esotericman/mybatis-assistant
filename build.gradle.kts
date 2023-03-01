plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "org.flmelody.mybatis"
version = "0.1"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IU") // Target IDE Platform
    pluginName.set("MybatisAssistant")
    plugins.set(listOf("com.intellij.database", "com.intellij.java", "org.jetbrains.kotlin",
            "com.intellij.spring", "com.intellij.spring.boot", "org.intellij.intelliLang"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("com.softwareloop:mybatis-generator-lombok-plugin:1.0")
    implementation("uk.com.robust-it:cloning:1.9.12")
    implementation("org.freemarker:freemarker:2.3.32")
    implementation("com.itranswarp:compiler:1.0")
}
