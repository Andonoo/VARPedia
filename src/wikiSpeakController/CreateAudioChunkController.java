package wikiSpeakController;

import java.util.LinkedHashMap;
import java.util.Map;

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
import wikiSpeakModel.MediaHelper;

/**
 * Class for controlling the audio chunk pop-up UI component.
 *
 */
public class CreateAudioChunkController {	
	private static Map<String, String> _voiceMap;
	private String _chunkName;
	private String _creationName;
	private Runnable _onAdd;
	
	@FXML private TextArea _selectedTextTA;
	@FXML private ComboBox<String> _voiceCombo;
	@FXML private Button _saveBtn;
	@FXML private Button _previewBtn;
	
	/**
	 * Method called on loading of the CreateAudioChunk popup scene.
	 */
	@FXML
    private void initialize() {
		_voiceMap = new LinkedHashMap<String, String>();
		_voiceMap.put("Default", "kal_diphone");
		_voiceMap.put("NZ(Male)", "akl_nz_jdt_diphone");
		_voiceMap.put("NZ(Female)", "akl_nz_cw_cg_cg");
		// List of festival voices
		for (String voiceName : _voiceMap.keySet()) {
			_voiceCombo.getItems().add(voiceName);
		}
		// Sets the combobox to select the first option by default
		_voiceCombo.getSelectionModel().selectFirst();
    }
	
	/**
	 * Listener method called when the audio preview button is clicked. Handles the event by previewing the
	 * audio chunk.
	 */
	@FXML
    private void previewBtnClicked() {
		setDisableButtons(true);
		String text = getText();
		String voiceOption = _voiceMap.get(_voiceCombo.getValue());
		String creationDir = ShellHelper.WrapString("./Creations/" + _creationName + "/");
		
		Thread worker = new Thread(()->{
			try {
				MediaHelper mh = new MediaHelper(creationDir);
				mh.previewAudioChunk(text, voiceOption);
			} catch (Exception e) {
				Platform.runLater(()-> showError("This voice can't pronounce the sentence :( Please change the voice..."));
			} finally {
				Platform.runLater(()-> setDisableButtons(false));
			}
		});
		worker.start();
    }
	
	/**
	 * Handler method called when the user clicks the save button on an audio chunk. Saves the user's 
	 * current audio chunk to be used in creation process.
	 * 
	 * @param event
	 */
	@FXML
    private void saveBtnClicked(ActionEvent event) {
		String text = getText();
		String voiceOption = _voiceMap.get(_voiceCombo.getValue());
		String creationAudioPath = "./Creations/" + _creationName + "/.temp/";
		setDisableButtons(true);
		Thread worker = new Thread(()->{
			try {
				MediaHelper mh = new MediaHelper(creationAudioPath);
				mh.createAudioChunk(text, voiceOption, _chunkName);
				
				Platform.runLater(()->{
					setDisableButtons(false);
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					this._onAdd.run();
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
		String text = _selectedTextTA.getText();
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
		_selectedTextTA.setText(text);
	}
	
	/**
	 * Defines the action upon adding
	 * @param r
	 */
	public void setOnAddAction(Runnable r) {
		this._onAdd = r;
	}
	
	/**
	 * Toggles visibility of save and preview buttons depending on parameter.
	 * 
	 * @param condition, true to disable. False to enable.
	 */
	private void setDisableButtons(Boolean condition) {
		this._saveBtn.setDisable(condition);
		this._previewBtn.setDisable(condition);
	}
}
