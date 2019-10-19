package wikiSpeakController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import wikiSpeakModel.GuessMedia;
import wikiSpeakModel.GuessingGameEngine;

public class GameStageController extends VideoPlayerController{
	@FXML Pane _videoPane;
	@FXML TextField _guessTF;
	GuessingGameEngine _engine;
	
	ToggleGroup radioButtonGroup = new ToggleGroup(); 
	
	@FXML
	private void initialize() {
		// runLater ensure the engine is set before doing anything
		Platform.runLater(()->{
			displayNextMedia();
		});
	}
	
	public void setEngine(GuessingGameEngine engine) {
		_engine = engine;
	}
	
	private void displayNextMedia() {
		if (_engine.hasNextMedia()){
			switch (_engine.getCategory()) {
				case Video:
					GuessMedia media = _engine.nextMedia();
					this.setVideo(media.getAudioVideo());
					break;
				case Audio:
					break;
				case Text:
					break;
			}
		} else {
			_engine.saveScoreBoard();
		}
	}
	
	@FXML
	private void guessBtnClicked(ActionEvent event) {
		System.out.println(_engine.checkGuess(_guessTF.getText()));
		displayNextMedia();
	}
	
	/**
	 * Show alert dialog
	 * 
	 * @param message
	 */
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
