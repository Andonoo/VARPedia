package wikiSpeakController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import wikiSpeak.Main;
import wikiSpeakModel.Playable;

/**
 * Class to control the PlayerUI components.
 * 
 * @author Andrew Donovan
 *
 */
public class VideoPlayerController {
	private boolean _paused;
	private Duration _duration;
	
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
	@FXML
	private Slider _volumeSlider;
	@FXML
	private Slider _playSlider;
	
	/**
	 * Method executed when play button is pressed.
	 */
	@FXML
	protected void handlePlay() {
		if (_paused) {
			_videoPlayer.play();
			_playButton.setText("Pause");
			_paused = false;
		} else {
			_videoPlayer.pause();
			_playButton.setText("Play");
			_paused = true;
		}
	}
	
	/**
	 * Method executed when pause button is pushed
	 */
	@FXML
	protected void handlePause() {
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
		setUpMediaPlayerListeners();
	}
	
	/**
	 * Sets the name of the creation to be played by this PlayerUI component.
	 * @param creationName
	 * @throws MalformedURLException 
	 */
	public void setVideo(File file) {
		try {
			_video = new Media(file.toURI().toURL().toString());			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		_videoPlayer = new MediaPlayer(_video);
		_videoPlayer.setAutoPlay(false);
		_videoDisplay.setMediaPlayer(_videoPlayer);
		setUpMediaPlayerListeners();
	}
	
	/**
	 * Sets the listeners for the media player and it's various user controls.
	 */
	private void setUpMediaPlayerListeners() {
		_paused = true;
		_volumeSlider.setValue(_videoPlayer.getVolume() * 100);
		_videoPlayer.setOnReady(() -> {
			_duration = _videoPlayer.getMedia().getDuration();
			
			setPlayerSliderListeners();
			setVolumeSliderListener();
			
			_videoPlayer.setOnEndOfMedia(new Runnable() {
	            public void run() {
	    			_videoPlayer.seek(_duration.ZERO);
	            }
	       });
		});
	}
	
	/**
	 * Set listener so that video volume mimics user slider input.
	 */
	private void setVolumeSliderListener() {
		_volumeSlider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
		       if (_volumeSlider.isValueChanging()) {
		    	   _videoPlayer.setVolume(_volumeSlider.getValue() / 100.0);
		       }
		    }
		});
	}
	
	/**
	 * Sets up the listeners so that the player slider and video mimic eachother's positions.
	 */
	private void setPlayerSliderListeners() {
		// Setting player slider to follow video position
		_videoPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
	        if (!_playSlider.isValueChanging()) {
	        	_playSlider.setValue((newTime.toSeconds()/ _duration.toSeconds()) * 100);
	        }
	    });
		
		// Setting video player to follow user input to slider
		_playSlider.valueProperty().addListener(new InvalidationListener() {
		    public void invalidated(Observable ov) {
		       if (_playSlider.isValueChanging()) {
		    	   _videoPlayer.seek(_duration.multiply(_playSlider.getValue() / 100.0));
		       }
		    }
		});
	}
}
