object ProjectVersions {
    const val openosrsVersion = "4.19.0"
    const val apiVersion = "^1.0.0"
}

object Libraries {
    private object Versions {
        const val guice = "4.2.2"
        const val javax = "1.3.2"
        const val lombok = "1.18.10"
        const val pf4j = "3.2.0"
        const val slf4j = "1.7.30"
        const val apacheCommonsText = "1.9"
        const val rxjava = "3.0.7"
        const val apacheCommonsLang = "3.8.1"
    }

    const val apacheCommonsLang = "org.apache.commons:commons-lang3:${Versions.apacheCommonsLang}"
    const val guice = "com.google.inject:guice:${Versions.guice}:no_aop"
    const val javax = "javax.annotation:javax.annotation-api:${Versions.javax}"
    const val lombok = "org.projectlombok:lombok:${Versions.lombok}"
    const val pf4j = "org.pf4j:pf4j:${Versions.pf4j}"
    const val slf4j = "org.slf4j:slf4j-api:${Versions.slf4j}"
    const val apacheCommonsText = "org.apache.commons:commons-text:${Versions.apacheCommonsText}"
    const val rxjava = "io.reactivex.rxjava3:rxjava:${Versions.rxjava}"
}
