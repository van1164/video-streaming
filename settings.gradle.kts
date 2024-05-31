plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "KoreanYoutube"
include("video")
include("user")
include("security")
include("live_stream")
include("util")
include("main")
include("comment")