plugins {
    id("java-library")
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
    compileOnly("org.spigotmc:spigot-api:1.14-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks {
    jar {
        from(project(":common").sourceSets.main.get().output)
        archiveFileName.set("crystade-spigot.jar")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
}
