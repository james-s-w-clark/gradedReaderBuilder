# Users
Head to the [Releases tab](https://github.com/IdiosApps/gradedReaderBuilder/releases) and get the latest build for your OS.

# Developers
In IntelliJ, clone from Git source. 
Gradle may not be happy (JRE / gradle support mismatch - something like that, forgot) - if so:
go to `File/Project Structure/Project` and set the SDK to Java `version 12`. *Refresh* gradle build. Everything should be good!

Open the Gradle tab, and under `application` do `run`. For now (Chinese 2nd/foreign language, English 1st/mother language) just hit `Build` :)

If you encounter a bug, please open an issue so we can make various bug fixes and performance improvements ;)
