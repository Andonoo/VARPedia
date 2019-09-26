package wikiSpeakController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import wikiSpeak.ShellHelper;

public class CreateAudioChunkController {
	@FXML
	private TextArea selectedTextTA;
	
	@FXML
	private ComboBox<String> voiceCombo;
	
	@FXML
    private void initialize() {
		voiceCombo.getItems().addAll(
		    "akl_nz_jdt_diphone",
		    "akl_nz_cw_cg_cg"
		);
    }
	
	@FXML
    private void previewBtnClicked() {
		String text = selectedTextTA.getText();
		String voiceOption = voiceCombo.getValue();
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > ./Creations/.temp/temp.txt", text);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o ./Creations/.temp/temp.wav -eval \'(voice_%s)\' < ./Creations/.temp/temp.txt",
						voiceOption);
				ShellHelper.execute(command);
				command = "play ./Creations/.temp/temp.wav";
				ShellHelper.execute(command);
				command = "rm ./Creations/.temp/temp.wav ./Creations/.temp/temp.txt";
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		worker.start();
    }
	
	@FXML
    private void saveBtnClicked(ActionEvent event) {
		
		String text = selectedTextTA.getText();
		String voiceOption = voiceCombo.getValue();
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > ./Creations/.temp/temp.txt", text);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o \"./Creations/.temp/chunk-$(date --iso-8601=seconds).wav\" -eval \'(voice_%s)\' < ./Creations/.temp/temp.txt",
						voiceOption);
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		worker.start();
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
	    stage.close();
    }
	
	public void setText(String text) {
		selectedTextTA.setText(text);
	}
}
