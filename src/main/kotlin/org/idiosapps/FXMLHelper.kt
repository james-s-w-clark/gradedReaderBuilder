package org.idiosapps

import javafx.scene.control.Alert
import javafx.scene.layout.Region

class FXMLHelper {
    companion object {
        fun createFittedAlert(exception: Exception): Alert {
            val message = ExceptionHelper.exceptionToMessage(exception)
            return createFittedAlert(message, Alert.AlertType.ERROR)
        }

        fun createFittedAlert(message: String, alertType: Alert.AlertType): Alert {
            val alert = Alert(alertType, message)
            alert.dialogPane.minWidth = Region.USE_PREF_SIZE // this does the fitting of the alert
            alert.dialogPane.minHeight = Region.USE_PREF_SIZE

            when (alertType) {
                Alert.AlertType.ERROR -> alert.headerText = "Error:"
                else -> alert.headerText = "Success:"
            }
            return alert
        }
    }
}