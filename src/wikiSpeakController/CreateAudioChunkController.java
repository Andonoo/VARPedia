package wikiSpeakController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class CreateAudioChunkController {
	@FXML
	private TextArea selectedTextTA;
	
	public void setText(String text) {
		selectedTextTA.setText(text);
	}
}
