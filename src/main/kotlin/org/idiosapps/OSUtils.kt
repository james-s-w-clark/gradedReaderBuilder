package org.idiosapps

import java.io.File
import java.io.IOException
import java.util.*

class OSUtils {
    companion object {
        const val WINDOWS: String = "windows"
        const val MACOS: String = "macOS"
        const val LINUX: String = "linux"
        const val UNDETECTED_OS: String = "undetected"

        const val WINDOWS_COMMAND_PREFIX = "cmd.exe /c"
        val LINUX_SHELL_PREFIX = arrayListOf("bash", "-c")
        val SPACE = " "


        const val XETEX = "XeTeX"
        const val PDFTEX = "pdfTeX"

        fun getOS(): String {
            val operatingSystem: String = System.getProperty("os.name").toLowerCase()

            when {
                operatingSystem.contains("windows") -> return WINDOWS
                operatingSystem.contains("mac") -> return MACOS
                operatingSystem.contains("linux") -> return LINUX
            }
            return UNDETECTED_OS
        }

        fun tryMakeOutputDir() {
            val outputFile = File("./output/")
            if (!outputFile.exists())
                outputFile.mkdir()
        }

        fun hasProgram(programToCheck: String) {
            val OS = getOS()
            val VERSION_PARAM = "--version"

            var command = ""
            if (OS == WINDOWS) {
                command = WINDOWS_COMMAND_PREFIX + SPACE // Windows needs this to be able to use e.g. XETEX
            }
            command += programToCheck + SPACE + VERSION_PARAM

            try {
                val process = Runtime.getRuntime().exec(command)
                val inputStream = process.inputStream
                val scanner = Scanner(inputStream, "UTF-8").useDelimiter("\\A")
                var haveRequiredProgram = false
                scanner.use {
                    lateinit var line: String
                    while (scanner.hasNext()) {
                        line = scanner.next()
                        if (line.contains(programToCheck)) { // sufficiently implies installation
                            haveRequiredProgram = true
                        }
                    }
                }
                if (!haveRequiredProgram) {
                    throw Exception(programToCheck)
                }
            } catch (exception: IOException) { // any other exception... maybe this outer-try is unnecessary
                exception.printStackTrace()
                throw Exception(exception)
            }
        }
    }
}