package wikiSpeakController;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeak.Playable;

/**
 * Class to control the PlayerUI components.
 * 
 * @author Andrew Donovan
 *
 */
public class VideoPlayerController {
	@FXML
	private Media _video;
	@FXML
	private MediaPlayer _videoPlayer;
	@FXML
	private MediaView _videoDisplay;
	@FXML
	private Button _playButton;
	@FXML
	private Text _creationTitle;

	/**
	 * Method executed when play button is pressed.
	 */
	@FXML
	private void handlePlay() {
		_videoPlayer.play();
	}
	
	/**
	 * Method executed when pause button is pushed
	 */
	@FXML
	private void handlePause() {
		_videoPlayer.pause();
	}
	
	/**
	 * Method called when user presses back button. Loads them back into
	 * play creations menu.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void handleBack(ActionEvent e) throws IOException {
		_videoPlayer.pause();
		Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("ViewCreations.fxml"));
		Parent layout = loader.load();
		Scene scene = new Scene(layout);
		stage.setScene(scene);
	}
	
	/**
	 * Sets the name of the creation to be played by this PlayerUI component.
	 * @param creationName
	 */
	public void setVideo(Playable playable) {
		_creationTitle.setText(playable.getPlayableName());
		File vid = new File(playable.getPath().replaceAll("\\s", "%20"));
		_video = new Media("file://" + vid.getAbsolutePath());
		_videoPlayer = new MediaPlayer(_video);
		_videoPlayer.setAutoPlay(false);
		_videoDisplay.setMediaPlayer(_videoPlayer);
	}
}
