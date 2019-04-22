package org.idiosapps

class OSUtils {
    companion object {
        fun getOS(): String {
            var operatingSystem : String = System.getProperty("os.name")

            if(operatingSystem.toLowerCase().contains("windows"))
                operatingSystem = "windows"
            else if (operatingSystem.toLowerCase().contains("mac"))
                operatingSystem = "mac"
            else if (operatingSystem.toLowerCase().contains("ubuntu"))
                operatingSystem = "ubuntu"
            return operatingSystem
        }
    }
}