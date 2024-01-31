import korlibs.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.gar.ruletaKorge"

// To enable all targets at once

	//targetAll()

// To enable targets based on properties/environment variables
	//targetDefault()

// To selectively enable targets
//    orientation = Orientation.LANDSCAPE
	targetJvm()
	targetJs()
	//targetDesktop()
	//targetIos()
    androidMinSdk = 23
    androidCompileSdk = 34
	targetAndroid()
	serializationJson()
}


dependencies {
    add("commonMainApi", project(":deps"))
    add("commonMainApi", "dev.gitlive:firebase-database:1.11.1")
    add("commonMainApi", "dev.gitlive:firebase-auth:1.11.1")
    add("androidMainApi", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0-RC2")
    add("jvmMainApi", "org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0-RC")
//    add("jvmMainApi", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    //add("commonMainApi", project(":korge-dragonbones"))
}

