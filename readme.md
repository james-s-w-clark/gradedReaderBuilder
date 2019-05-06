# Graded-Reader-Builder
Graded Reader Builder lets people create professional-quality graded readers from simple text input (story, vocab, characters, etc.). A wealth of high-quality books written by language learners, for language learners can be produced by this tool!

## Graded Reader Builder Input & Output

<img src="https://github.com/IdiosApps/gradedReaderBuilder_deprecated/blob/master/examples/Graded-Reader-Builder-OutputExample.png" width="400">
<img src="https://github.com/IdiosApps/gradedReaderBuilder_deprecated/blob/master/examples/Graded-Reader-Builder-Inputs.png" width="250">
<img src="https://github.com/IdiosApps/gradedReaderBuilder_deprecated/blob/master/examples/Graded-Reader-Builder-Vocab(CN-EN).png" width="250">

The example pdf is available [here](https://github.com/IdiosApps/gradedReaderBuilder_deprecated/blob/master/examples/ExampleGradedReader.pdf)

## Features
### Supported language pairs:
* L2 Mandarin (Hanzi/Hanzi(+pinyin in footers), L1 English
* L2 English, L1 Mandarin (TESTING)

#### Graded-reader features:
* All vocabulary words are superscripted in the story, with the form "page.vocab number".
* Each page with new vocabulary has left and right footers (which will split e.g. 3 words into 2 on the left, 1 on the right).
* Key names are underlined in the story.



# Users
Head to the [Releases tab](https://github.com/IdiosApps/gradedReaderBuilder/releases) and get the latest build for your OS.

# Developers
In IntelliJ, clone from Git source. 
Gradle may not be happy (JRE / gradle support mismatch - something like that, forgot) - if so:
go to `File/Project Structure/Project` and set the SDK to Java `version 12`. *Refresh* gradle build. Everything should be good!

Open the Gradle tab, and under `application` do `run`. For now (Chinese 2nd/foreign language, English 1st/mother language) just hit `Build` :)

In build.gradle, you will need to *comment out* these dependencies to use Gradle's `run` task:
``` 
    compile "org.openjfx:javafx-graphics:12.0.1:win"
    compile "org.openjfx:javafx-graphics:12.0.1:linux"
    compile "org.openjfx:javafx-graphics:12.0.1:mac"
```

To use Gradle's `shadowDistZip` and `launch4j`, you will need to have these dependencies enabled.

**If you encounter a bug, please open an issue so we can make various bug fixes and performance improvements ;)**
