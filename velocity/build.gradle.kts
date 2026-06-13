plugins {
    id("java-library")
    id("xyz.jpenilla.run-velocity") version "3.0.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    implementation(project(":common"))
}

tasks {
    jar {
        from(project(":common").sourceSets.main.get().output)
        archiveFileName.set("crystade-velocity.jar")
    }

    runVelocity {
        velocityVersion("3.1.1")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("velocity-plugin.json") {
            expand(props)
        }
    }
}
