package wikiSpeakController;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import wikiSpeak.Main;
import wikiSpeakModel.Playable;

public class SceneSwitcher {
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
	
	public static Parent getLayout(SceneOption sceneOption) throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(sceneOption.toString() + ".fxml"));
		Parent layout = loader.load();
		return layout;
	}
	
	public static Parent getPlayerLayout(Playable playable) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("PlayerUI.fxml"));
		Parent layout = loader.load();
		VideoPlayerController controller = loader.<VideoPlayerController>getController();
		controller.setVideo(playable);
		return layout;
	}
	
	public static Parent getGameAudioPlayer(String guessName) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("GameAudioPlayer.fxml"));
		Parent layout = loader.load();
		GameAudioPlayerController controller = loader.<GameAudioPlayerController>getController();
		controller.setAudio("./.Game/temp.mp3");
		return layout;
	}

}
