package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneSwitcher {
	public enum SceneOption {
		Main,
		View,
		VideoPlayer,
		Search,
		CreateAudio,
		CreateAudioChunk,
		FinalizeCreation
	}
	
	public static Parent getLayout(SceneOption sceneOption) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(sceneOption.toString() + ".fxml"));
		Parent layout = loader.load();
		return layout;
	}

}
