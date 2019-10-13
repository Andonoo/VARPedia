package wikiSpeakController;

import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.MediaHelper;

public class GameAudioPlayerController {
	private String _audioFilePath;
	@FXML private TextField guessTF;
	
	public void setAudio(String path) {
		_audioFilePath = path;
	}
	
	@FXML
	private void onListenBtnClicked(ActionEvent event) throws Exception {
		Thread worker = new Thread(()->{
			try {
				MediaHelper.playAudio("./.Game/temp.wav");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		});
		worker.start();
	}
	
	@FXML
	private void onCheckBtnClicked(ActionEvent event) {
		if (guessTF.getText().toLowerCase().equals("apple")) {
			showAlert("GOOD GUESS! Please go back now");
		} else {
			showAlert("Try again :(");
		}
	}
	
	private void showAlert(String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Error");
		alert.setHeaderText("Can't proceed");
		alert.setContentText(text);
		alert.showAndWait();
	}
	
	@FXML
	private void onBackBtnClicked(ActionEvent event) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Return to main menu");
		alert.setContentText("Are you sure you want to go home? All of the progress will be lost");
		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes){
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
			stage.setScene(scene);
		}
	}

}
