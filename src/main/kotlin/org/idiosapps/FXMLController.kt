package org.idiosapps

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import org.idiosapps.OSUtils.Companion.PDFTEX
import org.idiosapps.OSUtils.Companion.XETEX

class FXMLController {
    @FXML
    var l1Dropdown: JFXComboBox<String> = JFXComboBox()
    @FXML
    var l2Dropdown: JFXComboBox<String> = JFXComboBox()
    @FXML
    var storyState: Label = Label()
    @FXML
    var vocabState: Label = Label()
    @FXML
    var nameState: Label = Label()
    @FXML
    var progressBar: ProgressBar = ProgressBar()
    @FXML
    var buildButton: JFXButton = JFXButton()

    fun initialize() {
        loadLanguageComboBoxes()
    }

    @FXML
    fun buildButtonClicked() {
        try { // first check dependencies & inputs - have a good idea of what to expect
            OSUtils.hasProgram(PDFTEX)
            OSUtils.hasProgram(XETEX)
            OSUtils.tryMakeOutputDir() // Java will only make a new file if the parent folder exists (on Windows anyway)

            Filenames.checkInputs() // just check files exist
            clearInputStateLabels()
            checkInputStory() // check these inputs have contents
            checkInputVocab()
            checkInputNames()

            startBookBuildPipeline()
        } catch (exception: Exception) {
            exception.printStackTrace()

            val alert = FXMLHelper.createFittedAlert(exception)
            alert.show()
        }
    }

    // Start another thread with the real processing, so the UI doesn't freeze
    private fun startBookBuildPipeline() {
        val task: Task<Parent> = BuilderPipeline()
        progressBar.progressProperty().bind(task.progressProperty())
        val thread = Thread(task)
        thread.isDaemon = true
        thread.start()

        task.setOnRunning {
            buildButton.isDisable = true
            buildButton.style = "fx-background-color: blue"
        }
        task.setOnSucceeded {
            buildButton.isDisable = false
            buildButton.style = "fx-background-color: green"
        }
    }

    private fun clearInputStateLabels() {
        storyState.text = "..."
        vocabState.text = "..."
        nameState.text = "..."
    }

    private fun checkInputStory() {
        if (Filenames.hasContent(Filenames.inputStoryFilename)) {
            storyState.text = "OK"
        } else storyState.text = "No contents found"
    }

    private fun checkInputVocab() {
        if (Filenames.hasContent(Filenames.inputVocabFilename)) {
            vocabState.text = "OK"
        } else vocabState.text = "No contents found"
    }

    private fun checkInputNames() {
        if (Filenames.hasContent(Filenames.inputKeyNamesFilename)) {
            nameState.text = "OK"
        } else nameState.text = "No contents found"
    }

    private fun loadLanguageComboBoxes() {
        l1Dropdown.items = LanguageUtils.supportedL1
        l2Dropdown.items = LanguageUtils.supportedL2
    }
}