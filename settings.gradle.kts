rootProject.name = "plugins"

//include(":bankskiller")
//include(":alchtools")
//include(":waerialfishing")
//include(":wuriums")
//include(":wprayflick")
//include(":wbloods")
//include(":wloginspammer")
//include(":wreturntimer")

//include(":wautoprayutils")
//include(":praytest")
//include(":gauntletautopray")
//include(":hydraautopray")
//include(":nightmareautopray")
//include(":infernoautopray")

//include(":winvoker")
//include(":wraturn")

include(":autotanker")
include(":playernotifier")
include(":cwa")
include(":keyspammer")
include(":wautotyper")
include(":missingplayers")
include(":autocaster")
include(":wpneck")
include(":wuav")
include(":hideattack")
include(":playerindicatorssnipelist")

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "$name.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}