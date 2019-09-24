package wikiSpeakController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateAudioController {
	private String wikiContent;
	
	@FXML
	private TextArea wikiTextTA;
	
	public void setText(String wikiContent){
		this.wikiContent = wikiContent;
		wikiTextTA.setText(wikiContent);
	}
	
	@FXML
    public void initialize() {
        wikiTextTA.setText(wikiContent);
    }
}
