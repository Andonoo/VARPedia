package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import wikiSpeakController.CreateAudioController;

public class CreateAudio {
	public Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(CreateAudio.class.getResource("CreateAudio.fxml"));
		CreateAudioController controller = loader.<CreateAudioController>getController();
		Parent layout = loader.load();
		return layout;
	}
}
