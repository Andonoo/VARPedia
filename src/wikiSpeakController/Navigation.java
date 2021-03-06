package wikiSpeakController;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher.SceneOption;

/**
 * List of common methods used in many UI Controller classes
 * 
 * @author Xiaobin Lin
 */
public class Navigation {
	/**
	 * Attempts to go home by switching scene.
	 * @param event
	 * @throws IOException
	 */
	@FXML
	protected void onHomeBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
		stage.setScene(scene);
	}
	
	/**
	 * Show alert dialog
	 * 
	 * @param message
	 */
	protected void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	/**
	 * Show information dialog
	 * 
	 * @param message
	 */
	protected void showInfo(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
