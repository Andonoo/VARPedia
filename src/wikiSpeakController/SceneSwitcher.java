package wikiSpeakController;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import wikiSpeak.Main;
import wikiSpeakModel.Playable;

/**
 * Class to handle the retrieval and loading of fxml files into layouts to be displayed to the users.
 */
public class SceneSwitcher {
	// Enum for different UI scenes
	public enum SceneOption {
		Main,
		View,
		GameSetup,
		VideoPlayer,
		Search,
		CreateAudio,
		CreateAudioChunk,
		ViewCreations,
		FinalizeCreation,
		EndGameScoreBoard,
		Leaderboard
	}
	
	/**
	 * Returns the appropriate layout for the provided SceneOption by loading its
	 * fxml file.
	 * 
	 * @param sceneOption
	 * @return
	 * @throws IOException
	 */
	public static Parent getLayout(SceneOption sceneOption) throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(sceneOption.toString() + ".fxml"));
		Parent layout = loader.load();
		return layout;
	}
	
	/**
	 * Returns the video player layout for the provided playable element.
	 * 
	 * @param playable
	 * @return
	 * @throws IOException
	 */
	public static Parent getPlayerLayout(Playable playable) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("PlayerUI.fxml"));
		Parent layout = loader.load();
		VideoPlayerController controller = loader.<VideoPlayerController>getController();
		controller.setVideo(playable);
		return layout;
	}
}
