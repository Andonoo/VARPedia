package wikiSpeakController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.AudioChunk;
import wikiSpeakModel.Creation;
import wikiSpeakModel.Playable;

/**
 * Controller class for audio creation UI component.
 * 
 */
public class CreateAudioController extends Navigation{
	private String _creationName;
	private String _searchTerm;
	private String _wikiContent;
	private int _chunkCount;
	
	@FXML private Button _nxtButton;
	@FXML private TextArea _wikiTextTA;
	@FXML private TextArea _selectedTextTA;
	@FXML private TableView _audioChunkTV;
	@FXML private Button _addAudioChunkBtn;
	
	/**
	 * Finds the number of words in a string
	 * @param text
	 * @return
	 */
	private int countWords(String text) {
		int count = 0;
		// Find any non-white spaces
		Pattern pattern = Pattern.compile("\\S+");
		Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
        	count++;        	
        }
        return count;
	}
	
	@FXML
    public void initialize() {
		_nxtButton.setDisable(true);
        _wikiTextTA.setText(_wikiContent);
        _wikiTextTA.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
        	if (countWords(newValue) < 40) {
        		_selectedTextTA.setText(newValue);   
        		_addAudioChunkBtn.setDisable(false);    		
        	} else {
        		_selectedTextTA.setText("You've selected too many words."); 
        		_addAudioChunkBtn.setDisable(true);
        	}
        });
        
        // Ensure only enable the next button when there is something to play
        _audioChunkTV.getItems().addListener(new ListChangeListener() {
			@Override
			public void onChanged(Change arg0) {
				if (_audioChunkTV.getItems().size() > 0) {
					_nxtButton.setDisable(false);
				} else {
					_nxtButton.setDisable(true);
				}	
			}
        });
        
        loadColumns();
        refreshTableAsync();
        _chunkCount = 0;
    }
	
	/**
	 * Loads the column with headings
	 */
	private void loadColumns() {
		// Create columns for the UI
		List<String> creationFieldNames = Arrays.asList("PlayableName", "duration", "play", "delete");
		List<String> tableColumnNames = Arrays.asList("Name", "Duration", "Play", "Delete");
		double[] widthMultiplier = {0.49, 0.5/3, 0.5/3, 0.5/3};
		List<TableColumn<String, Creation>> columns = new ArrayList<TableColumn<String, Creation>>();
		for (int i = 0; i < creationFieldNames.size(); i++) {
			TableColumn<String, Creation> col = new TableColumn<>(tableColumnNames.get(i));
			col.setCellValueFactory(new PropertyValueFactory<>(creationFieldNames.get(i)));
			col.prefWidthProperty().bind(_audioChunkTV.widthProperty().multiply(widthMultiplier[i]));
			columns.add(col);
		}
		_audioChunkTV.getColumns().addAll(columns);
	}
	
	@FXML
	private void onAddBtnClicked(ActionEvent event) throws IOException {
		Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("CreateAudioChunk.fxml"));
		Parent layout = loader.load();

		String chunkName = _creationName + _chunkCount;
		String text = _wikiTextTA.getSelectedText();
		CreateAudioChunkController controller = loader.<CreateAudioChunkController>getController();
		controller.setContent(_creationName, chunkName, text);
		controller.setOnAddAction(()->onChunkCreation());
		Scene scene = new Scene(layout);
		
		Stage modal = new Stage();
		modal.initOwner(parentStage);
		modal.initModality(Modality.APPLICATION_MODAL); 
		modal.setScene(scene);
		modal.showAndWait();
	}
	
	private void onChunkCreation() {
		refreshTableAsync();
		_chunkCount++;
	}
	
	@FXML
	private void onNextButtonClicked(ActionEvent event) throws IOException {
		Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("FinalizeCreation.fxml"));
		Parent layout = loader.load();
		
		FinalizeCreationController controller = loader.<FinalizeCreationController>getController();
		controller.setCreationInfo(_creationName, _searchTerm);
		Scene scene = new Scene(layout);
		
		parentStage.setScene(scene);
	}
	
	/**
	 * Refresh the table when there are changes to the content
	 */
	private void refreshTableAsync() {
		_audioChunkTV.getItems().clear();
		_audioChunkTV.setPlaceholder(new Label("Loading..."));
		Thread worker = new Thread(()->{
			List<Playable> creations = getCreations();
			Platform.runLater(()->{
				// Hint that there are no creations
				_audioChunkTV.setPlaceholder(new Label("There are no audio chunks"));

				// If there are creations, placeholder will not be shown
				for (int i = 0; i < creations.size(); i++) {
					_audioChunkTV.getItems().add(creations.get(i));
				}
			});
		});
		worker.start();
	}
	
	/***
	 * Get creations using bash by getting contents from ./Creations
	 * @return
	 * @throws RuntimeException
	 */
	private List<Playable> getCreations() throws RuntimeException {
		List<String> playableNames = new ArrayList<String>();
		List<Playable> playables = new ArrayList<Playable>();
		try {
            String command = String.format("ls -a ./Creations/%s/.temp/%s* 2> /dev/null | grep -Po \".+.wav\"", ShellHelper.WrapString(_creationName), ShellHelper.WrapString(_creationName));
            playableNames = ShellHelper.execute(command);
		} catch (Exception e) {
		    // Return empty list of creations to indicate there are no creations
			return new ArrayList<Playable>();
		}
		
		// Create a list of Playable objects
		for (int i = 0; i < playableNames.size(); i++) {
			AudioChunk creation = new AudioChunk(playableNames.get(i), ()->{refreshTableAsync();});
			playables.add(creation);
		}
		
		return playables;
	}

	/**
	 * Set the context for CreateAudio UI
	 * @param creationName
	 * @param searchTerm
	 * @param wikiContent
	 */
	public void setCreationData(String creationName, String searchTerm, String wikiContent) {
		_creationName = creationName;
		_searchTerm = searchTerm;
		_wikiContent = wikiContent;
		_wikiTextTA.setText(_wikiContent);
	}
}
