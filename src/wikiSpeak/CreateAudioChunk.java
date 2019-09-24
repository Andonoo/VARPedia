package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import wikiSpeakController.CreateAudioChunkController;
import wikiSpeakController.CreateAudioController;

public class CreateAudioChunk {
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(CreateAudio.class.getResource("CreateAudioChunk.fxml"));
		Parent layout = loader.load();
		return layout;
	}
}
