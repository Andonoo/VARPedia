package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import wikiSpeakController.VideoPlayerController;

/**
 * Class used to retrieve a UI component to play a creation.
 * 
 * @author Andrew Donovan
 */
public class PlayerUI {
	public static Parent getLayout(String creationName) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("PlayerUI.fxml"));
		Parent layout = loader.load();
		VideoPlayerController controller = loader.<VideoPlayerController>getController();
		controller.setVideo(creationName);
		return layout;
	}
}
