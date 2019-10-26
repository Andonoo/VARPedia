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
	
	private boolean displayNextMedia() {
		_videoPane.setVisible(false);
		_audioPane.setVisible(false);
		_textPane.setVisible(false);
		prepareForNewQuestion();
		
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
	
	private void setGuessText(String text) {
		_guessTA.setText(text);
	}
	
	private void prepareForNewQuestion() {
		_guessTF.clear();
	}
	
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
	
	@FXML
	private void onHomeBtnClicked(ActionEvent event) throws IOException {
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
