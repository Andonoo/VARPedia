package wikiSpeakController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Class for controlling the audio chunk pop-up UI component.
 *
 */
public class CreateAudioChunkController {	
	private String _chunkName;
	private String _creationName;
	private Runnable onAdd;
	
	@FXML
	private TextArea selectedTextTA;
	
	@FXML
	private ComboBox<String> voiceCombo;
	
	@FXML
	private Button saveBtn;
	
	@FXML
	private Button previewBtn;
	
	@FXML
    private void initialize() {
		// List of festival voices
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
		setDisableButtons(true);
		String text = getText();
		String voiceOption = voiceCombo.getValue();
		String creationPath = ShellHelper.WrapString("./Creations/" + _creationName);
		
		Thread worker = new Thread(()->{
			try {
				// Create a wav file using text2wave
				String command = String.format("echo \"%s\" > %s/.temp.txt", text, creationPath);
				ShellHelper.execute(command);
				command = String.format("text2wave -o %s/.temp.wav -eval \'(voice_%s)\' < %s/.temp.txt",
						creationPath, voiceOption, creationPath);
				ShellHelper.execute(command);
				command = String.format("play %s/.temp.wav", creationPath);
				ShellHelper.execute(command);
			} catch (Exception e) {
				Platform.runLater(()->showError("This voice can't pronounce the sentence :( Please change the voice..."));
			} finally {
				Platform.runLater(()->setDisableButtons(false));
			}
		});
		worker.start();
    }
	
	@FXML
    private void saveBtnClicked(ActionEvent event) {
		String text = getText();
		String voiceOption = voiceCombo.getValue();
		String creationPath = ShellHelper.WrapString("./Creations/" + _creationName);
		setDisableButtons(true);
		Thread worker = new Thread(()->{
			try {
				// Save the audio chunk file
				String command = String.format("echo \"%s\" > %s/.temp.txt", text, creationPath);
				ShellHelper.execute(command);
				command = String.format("text2wave -o %s/.temp/%s.wav -eval \'(voice_%s)\' < ./Creations/%s/.temp.txt",
						creationPath, ShellHelper.WrapString(_chunkName), voiceOption, ShellHelper.WrapString(_creationName));
				ShellHelper.execute(command);
				
				Platform.runLater(()->{
					setDisableButtons(false);
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					this.onAdd.run();
				    stage.close();
				});
				
			} catch (Exception e) {
				Platform.runLater(()->{
					showError("This voice can't pronounce the sentence :( Please change the voice...");
					setDisableButtons(false);
				});
			}
		});
		worker.start();
    }
	
	/**
	 * Get text from user input
	 * @return
	 */
	private String getText() {
		String text = selectedTextTA.getText();
		// Filter out all special character that the text synthesizer can't speak
		text = text.replaceAll("[^a-zA-Z\\s\\,\\.0-9\\-\\']", " ");
		return text;
	}
	
	/**
	 * Show a javafx alert box
	 * @param text
	 */
	private void showError(String text) {
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setContentText(text);
		errorAlert.showAndWait();
	}
	
	/**
	 * Set the content of the AudioChunk box
	 * @param creationName
	 * @param chunkName
	 * @param text
	 */
	public void setContent(String creationName, String chunkName, String text) {
		_creationName = creationName;
		_chunkName = chunkName;
		selectedTextTA.setText(text);
	}
	
	/**
	 * Defines the action upon adding
	 * @param r
	 */
	public void setOnAddAction(Runnable r) {
		this.onAdd = r;
	}
	
	private void setDisableButtons(Boolean condition) {
		this.saveBtn.setDisable(condition);
		this.previewBtn.setDisable(condition);
	}
	
	
}
