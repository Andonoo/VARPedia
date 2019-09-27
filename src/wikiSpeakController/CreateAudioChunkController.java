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
	
	private String creationName;
	
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
		String creationPath = "./Creations/" + creationName;
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > %s/temp.txt", text, creationPath);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o %s/temp.wav -eval \'(voice_%s)\' < %s/temp.txt",
						creationPath, voiceOption, creationPath);
				ShellHelper.execute(command);
				command = String.format("play %s/temp.wav", creationPath);
				ShellHelper.execute(command);
				command = String.format("rm %s/temp.wav %s/temp.txt", creationPath, creationPath);
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
		String creationPath = "./Creations/" + creationName;
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > %s/temp.txt", text, creationPath);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o \"%s/chunk-$(date --iso-8601=seconds).wav\" -eval \'(voice_%s)\' < %s/temp.txt",
						creationPath, voiceOption, creationPath);
				ShellHelper.execute(command);
				command = String.format("rm %s/temp.txt", creationPath);
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
	
	public void setCreationName(String creationName) {
		this.creationName = creationName;
	}
}
