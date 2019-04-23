package org.idiosapps

class OSUtils {
    companion object {
        var WINDOWS: String = "windows"
        var MACOS: String = "macOS"
        var LINUX: String = "linux"
        var UNDETECTED_OS: String = "undetected"

        fun getOS(): String {
            val operatingSystem : String = System.getProperty("os.name").toLowerCase()

            when {
                operatingSystem.contains("windows") -> return WINDOWS
                operatingSystem.contains("mac") -> return MACOS
                operatingSystem.contains("linux") -> return LINUX
            }
            return UNDETECTED_OS
        }
    }
}