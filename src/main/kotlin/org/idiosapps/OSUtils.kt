package org.idiosapps

class OSUtils {
    companion object {
        var WINDOWS: String = "windows"
        var MACOS: String = "macOS"
        var UBUNTU: String = "ubuntu"
        var UNDETECTED_OS: String = "undetected OS"

        fun getOS(): String {
            val operatingSystem : String = System.getProperty("os.name").toLowerCase()

            when {
                operatingSystem.contains("windows") -> return WINDOWS
                operatingSystem.contains("mac") -> return MACOS
                operatingSystem.contains("ubuntu") -> return UBUNTU
            }
            return UNDETECTED_OS
        }
    }
}