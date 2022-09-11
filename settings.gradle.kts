pluginManagement {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
  }
}

include(":blurhash")
include(":sample-android")
include(":sample-jvm")
