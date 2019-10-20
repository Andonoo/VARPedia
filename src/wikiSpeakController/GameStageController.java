package wikiSpeakController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import wikiSpeakModel.GuessMedia;
import wikiSpeakModel.GuessingGameEngine;
import wikiSpeakModel.MediaType;

public class GameStageController extends VideoPlayerController{
	@FXML Pane _videoPane;
	@FXML Pane _textPane;
	@FXML TextField _guessTF;
	@FXML TextArea _guessTA;
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
		_videoPane.setVisible(false);
		_textPane.setVisible(false);
		prepareForNewQuestion();
		
		if (_engine.hasNextMedia()){
			GuessMedia media;
			switch (_engine.getCategory()) {
				case Video: case Audio:
					_videoPane.setVisible(true);
					media = _engine.nextMedia();
					this.setVideo(media.getAudioVideo());
					break;
				case Text:
					_textPane.setVisible(true);
					setGuessText(_engine.nextMedia().getText());
					break;
			}
		} else {
			_engine.saveScoreBoard();
			showAlert("END", "The end of the quiz", AlertType.INFORMATION);
		}
	}
	
	private void setGuessText(String text) {
		_guessTA.setText(text);
	}
	
	private void prepareForNewQuestion() {
		_guessTF.clear();
	}
	
	@FXML
	private void guessBtnClicked(ActionEvent event) {
		checkAnswer(_guessTF.getText());
		displayNextMedia();
	}
	
	private void checkAnswer(String guess) {
		if (!(_engine.getCategory() == MediaType.Text)) {
			this.handlePause();
		}
		boolean isCorrent = _engine.checkGuess(guess);
		String message;
		AlertType alertType;
		if (isCorrent) {
			message = "Congratulations! \nYour answer is correct";
			alertType = AlertType.INFORMATION;
		} else {
			message = String.format("You guessed %s, but answer was %s", guess, _engine.getAnswer());
			alertType = AlertType.ERROR;
		}
		showAlert("Guess Result", message, alertType);
	}
	
	/**
	 * Show alert dialog
	 * 
	 * @param message
	 */
	private void showAlert(String title, String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
