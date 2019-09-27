package wikiSpeakController;

import javafx.application.Platform;
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
	private Runnable onAdd;
	
	@FXML
	private ComboBox<String> voiceCombo;
	
	@FXML
    private void initialize() {
		voiceCombo.getItems().addAll(
		    "akl_nz_jdt_diphone",
		    "akl_nz_cw_cg_cg"
		);
		// Sets the combobox to select the first option by default
		voiceCombo.getSelectionModel().selectFirst();
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
				command = String.format("rm %s/temp.wav %s/temp.txt &2> /dev/null", creationPath, creationPath);
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
				
				Platform.runLater(()->{
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					this.onAdd.run();
				    stage.close();
				});
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		worker.start();
    }
	
	public void setText(String text) {
		selectedTextTA.setText(text);
	}
	
	public void setCreationName(String creationName) {
		this.creationName = creationName;
	}
	
	public void setOnAddAction(Runnable r) {
		this.onAdd = r;
	}
}
