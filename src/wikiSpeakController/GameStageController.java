package wikiSpeakController;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.GuessMedia;
import wikiSpeakModel.GuessingGameEngine;
import wikiSpeakModel.MediaType;

/**
 * Class to control the display of guess media components to the user.
 * 
 * @author Andrew Donova, Xiaobin Lin
 */
public class GameStageController extends VideoPlayerController {
	@FXML Pane _videoPane;
	@FXML Pane _textPane;
	@FXML Pane _audioPane;
	@FXML TextField _guessTF;
	@FXML TextArea _guessTA;
	GuessingGameEngine _engine;
	
	ToggleGroup radioButtonGroup = new ToggleGroup(); 
	
	/**
	 * Method called on launch of the game.
	 */
	@FXML
	private void initialize() {
		// runLater ensure the engine is set before doing anything
		Platform.runLater(()->{
			displayNextMedia();
		});
	}
	
	/**
	 * Sets the game engine for this game instance.
	 * 
	 * @param engine
	 */
	public void setEngine(GuessingGameEngine engine) {
		_engine = engine;
	}
	
	/**
	 * Tests to ensure the game engine has another media element and if so retrieves it.
	 *  
	 * @return true if new media has been successfully retrieved.
	 */
	private boolean displayNextMedia() {
		_videoPane.setVisible(false);
		_audioPane.setVisible(false);
		_textPane.setVisible(false);
		prepareForNewQuestion();
		
		/* If the engine has another media component, retrieves it and depending on the engine's game type 
		  sets the appropriate scene */
		if (_engine.hasNextMedia()){
			GuessMedia media;
			switch (_engine.getCategory()) {
				case Video:
					_videoPane.setVisible(true);
					media = _engine.nextMedia();
					this.setVideo(media.getAudioVideo());
					break;
				case Audio:
					// Audio section also used the player to play the audio
					_videoPane.setVisible(true);
					_audioPane.setVisible(true);
					media = _engine.nextMedia();
					this.setVideo(media.getAudioVideo());
					break;
				case Text:
					_textPane.setVisible(true);
					_videoPane.setVisible(false);
					setGuessText(_engine.nextMedia().getText());
					break;
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the text within the guess text area.
	 * 
	 * @param text
	 */
	private void setGuessText(String text) {
		_guessTA.setText(text);
	}
	
	/**
	 * Clears the user's input field on new round.
	 */
	private void prepareForNewQuestion() {
		_guessTF.clear();
	}
	
	/**
	 * Listener class for guess button. Executes engine logic to determine if guess was correct. 
	 * 
	 * @param event
	 */
	@FXML
	private void guessBtnClicked(ActionEvent event) {
		checkAnswer(_guessTF.getText());
		if (!displayNextMedia()) {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("EndGameScoreBoard.fxml"));
				Parent layout = loader.load();
				EndGameScoreBoardController controller = loader.<EndGameScoreBoardController>getController();
				controller.setPlayerScore(_engine.getPlayerScore());
				Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
				Scene scene = new Scene(layout);
				stage.setScene(scene);
				_engine.saveScoreBoard();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Determines if the user's guess was correct.
	 * 
	 * @param guess
	 */
	private void checkAnswer(String guess) {
		if (!(_engine.getCategory() == MediaType.Text)) {
			this.forcePause();
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
