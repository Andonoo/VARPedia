package wikiSpeakController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import wikiSpeak.ShellHelper;

/**
 * Class for controlling the audio chunk pop-up UI component.
 *
 */
public class CreateAudioChunkController {	
	private String _chunkName;
	private String _creationName;
	
	@FXML
	private TextArea selectedTextTA;
	
	private Runnable onAdd;
	
	@FXML
	private ComboBox<String> voiceCombo;
	
	@FXML
    private void initialize() {
		voiceCombo.getItems().addAll(
			"kal_diphone",
		    "akl_nz_jdt_diphone",
		    "akl_nz_cw_cg_cg"
		);
		// Sets the combobox to select the first option by default
		voiceCombo.getSelectionModel().selectFirst();
    }
	
	@FXML
    private void previewBtnClicked() {
		String text = getText();
		String voiceOption = voiceCombo.getValue();
		String creationPath = "./Creations/" + _creationName;
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > %s/.temp.txt", text, creationPath);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o %s/.temp.wav -eval \'(voice_%s)\' < %s/.temp.txt",
						creationPath, voiceOption, creationPath);
				ShellHelper.execute(command);
				command = String.format("play %s/.temp.wav", creationPath);
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Platform.runLater(()->showError("This voice can't pronounce the sentence :( Please change the voice..."));
			}
		});
		worker.start();
    }
	
	@FXML
    private void saveBtnClicked(ActionEvent event) {
		String text = getText();
		String voiceOption = voiceCombo.getValue();
		String creationPath = "./Creations/" + _creationName;
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > %s/.temp.txt", text, creationPath);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o \"%s/.temp/%s.wav\" -eval \'(voice_%s)\' < ./Creations/%s/.temp.txt",
						creationPath, _chunkName, voiceOption, _creationName);
				ShellHelper.execute(command);
				
				Platform.runLater(()->{
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					this.onAdd.run();
				    stage.close();
				});
				
			} catch (Exception e) {
				Platform.runLater(()->showError("This voice can't pronounce the sentence :( Please change the voice..."));
			}
		});
		worker.start();
    }
	
	private String getText() {
		String text = selectedTextTA.getText();
		text = text.replaceAll("[^a-zA-Z\\s\\,\\.0-9\\-\\']", " ");
		return text;
	}
	
	private void showError(String text) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setContentText(text);
		errorAlert.showAndWait();
	}
	
	public void setContent(String creationName, String chunkName, String text) {
		_creationName = creationName;
		_chunkName = chunkName;
		selectedTextTA.setText(text);
	}
	
	public void setOnAddAction(Runnable r) {
		this.onAdd = r;
	}
}
