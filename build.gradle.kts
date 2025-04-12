import buildlogic.Utils

plugins {
    id("build.fabric")
    id("build.publish")
}

Utils.setupResources(project, rootProject, "fabric.mod.json")

sourceSets {
    register("testmod") {

        val main = sourceSets.main.get()

        compileClasspath += main.compileClasspath
        runtimeClasspath += main.runtimeClasspath
    }
}

dependencies {

    minecraft("com.mojang:minecraft:${project.properties["minecraft-version"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.properties["fabric-loader-version"]}")

    // Fabric API
    val apiModules = listOf(
        "fabric-api-base",
        "fabric-resource-loader-v0"
    )
    for(mod in apiModules) {
        modApi(include(fabricApi.module(mod, "${project.properties["fabric-api-version"]}"))!!)
    }

    compileOnly(libs.jetbrains.annotations)

    "testmodRuntimeOnly"("org.wallentines:databridge:0.7.0")
    "testmodImplementation"(sourceSets.main.get().output)
}


loom {
    mods {
        register(project.name + "-testmod") {
            sourceSet(sourceSets["testmod"])
        }
    }
    runs {
        register("testmodServer") {
            server()
            ideConfigGenerated(false)
            runDir = "run/testserver"
            name = "Testmod Server"
            source(sourceSets.getByName("testmod"))
        }
    }
}
