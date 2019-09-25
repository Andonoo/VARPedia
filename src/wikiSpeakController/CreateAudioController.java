package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wikiSpeak.CreateAudioChunk;
import wikiSpeak.Main;
import wikiSpeak.Search;

public class CreateAudioController {
	private String wikiContent;
	
	@FXML
	private TextArea wikiTextTA;
	
	@FXML
	private TextArea selectedTextTA;
	
	public void setText(String wikiContent) {
		this.wikiContent = wikiContent;
		wikiTextTA.setText(wikiContent);
	}
	
	private int countWords(String text) {
		int count = 0;
		// Remove the leading and ending spaces
		text = text.trim();
		
		// Count the number of spaces
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				count++;
			}
		}
		
		return count;
	}
	
	@FXML
    public void initialize() {
        wikiTextTA.setText(wikiContent);
        
        wikiTextTA.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
        	if (countWords(newValue) < 40) {
        		selectedTextTA.setText(newValue);        		
        	} else {
        		selectedTextTA.setText("YOU SELECTED TOO MANY WORDS"); 
        	}
        });
    }
	
	@FXML
	private void onAddBtnClicked(ActionEvent event) throws IOException {
		Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("CreateAudioChunk.fxml"));
		Parent layout = loader.load();

		String text = wikiTextTA.getSelectedText();
		CreateAudioChunkController controller = loader.<CreateAudioChunkController>getController();
		controller.setText(text);
		Scene scene = new Scene(layout);
		
		Stage modal = new Stage();
		modal.initOwner(parentStage);
		modal.initModality(Modality.APPLICATION_MODAL); 
		modal.setScene(scene);
		modal.showAndWait();
	}
}
