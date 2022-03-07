import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
}

group = "com.hpfxd.chatfilter"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://repo.hpfxd.com/releases/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    implementation("com.hpfxd.natelib:natelib-bukkit:3.1")
    implementation("com.google.inject:guice:5.1.0")
}

bukkit {
    main = "com.hpfxd.chatfilter.ChatFilterPlugin"
    author = "hpfxd"
    softDepend = listOf("PlaceholderAPI")

    permissions {
        register("chatfilter.bypass") {
            description = "Allows players to bypass the filter if enabled in the config."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("chatfilter.notifications") {
            description = "Allows players to receive all filter notifications."
            default = BukkitPluginDescription.Permission.Default.OP

            children = listOf(
                "chatfilter.notifications.block",
                "chatfilter.notifications.fake",
                "chatfilter.notifications.censor",
            )
        }

        register("chatfilter.notifications.block") {
            description = "Allows players to receive notifications when a message is blocked."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("chatfilter.notifications.fake") {
            description = "Allows players to receive notifications when a message is faked."
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("chatfilter.notifications.censor") {
            description = "Allows players to receive notifications when a message is censored."
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        val libsPkg = "com.hpfxd.chatfilter.libs"

        relocate("com.hpfxd.natelib", "$libsPkg.natelib")
        relocate("com.hpfxd.gnms", "$libsPkg.gnms")
        relocate("io.leangen.geantyref", "$libsPkg.geantyref")
        relocate("org.spongepowered", "$libsPkg.spongepowered")
        relocate("com.typesafe", "$libsPkg.typesafe")
        relocate("net.kyori", "$libsPkg.kyori")
        relocate("org.aopalliance", "$libsPkg.aopalliance")
        relocate("org.checkerframework", "$libsPkg.checkerframework")
        relocate("javax", "$libsPkg.javax")
        relocate("com.google.inject", "$libsPkg.google.inject")
        relocate("com.google.errorprone", "$libsPkg.google.errorprone")
        relocate("com.google.j2objc", "$libsPkg.google.j2objc")
        relocate("com.google.thirdparty", "$libsPkg.google.thirdparty")
        // 1.8.x doesn't include a recent enough version for guice
        relocate("com.google.common", "$libsPkg.google.common")

        exclude("META-INF", "META-INF/**")

        // minecraft should include these libraries
        exclude("com/google/gson/**/*")
        exclude("org/slf4j/**/*")
    }

    named("build") { dependsOn(named("shadowJar")) }
}
