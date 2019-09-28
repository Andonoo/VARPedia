package wikiSpeakController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wikiSpeak.AudioChunk;
import wikiSpeak.CreateAudioChunk;
import wikiSpeak.Creation;
import wikiSpeak.FlickrHelper;
import wikiSpeak.Main;
import wikiSpeak.Playable;
import wikiSpeak.Search;
import wikiSpeak.ShellHelper;

public class CreateAudioController {
	private String _creationName;
	private String _searchTerm;
	private String _wikiContent;
	private int _chunkCount;
	
	@FXML
	private Button _nxtButton;
	
	@FXML
	private TextArea wikiTextTA;
	
	@FXML
	private TextArea selectedTextTA;
	
	@FXML
	private TableView audioChunkTV;
	
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
		_nxtButton.setDisable(true);
        wikiTextTA.setText(_wikiContent);
        wikiTextTA.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
        	if (countWords(newValue) < 40) {
        		selectedTextTA.setText(newValue);        		
        	} else {
        		selectedTextTA.setText("YOU SELECTED TOO MANY WORDS"); 
        	}
        });
        loadData();
        refreshTableAsync();
        _chunkCount = 0;
    }
	
	private void loadData() {
		// Create columns for the UI
		List<String> creationFieldNames = Arrays.asList("PlayableName", "duration", "play", "delete");
		List<String> tableColumnNames = Arrays.asList("Name", "Duration", "Play", "Delete");
		double[] widthMultiplier = {0.4, 0.5/3, 0.5/3, 0.5/3};
		List<TableColumn<String, Creation>> columns = new ArrayList<TableColumn<String, Creation>>();
		for (int i = 0; i < creationFieldNames.size(); i++) {
			TableColumn<String, Creation> col = new TableColumn<>(tableColumnNames.get(i));
			col.setCellValueFactory(new PropertyValueFactory<>(creationFieldNames.get(i)));
			col.prefWidthProperty().bind(audioChunkTV.widthProperty().multiply(widthMultiplier[i]));
			columns.add(col);
		}
		audioChunkTV.getColumns().addAll(columns);
	}
	
	@FXML
	private void onAddBtnClicked(ActionEvent event) throws IOException {
		Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("CreateAudioChunk.fxml"));
		Parent layout = loader.load();

		String chunkName = _creationName + _chunkCount;
		String text = wikiTextTA.getSelectedText();
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
		_nxtButton.setDisable(false);
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
	
	private void refreshTableAsync() {
		audioChunkTV.getItems().clear();
		audioChunkTV.setPlaceholder(new Label("Loading..."));
		Thread worker = new Thread(()->{
			List<Playable> creations = getCreations();
			Platform.runLater(()->{
				// Hint that there are no creations
				audioChunkTV.setPlaceholder(new Label("There are no audio chunks"));

				// If there are creations, placeholder will not be shown
				for (int i = 0; i < creations.size(); i++) {
					audioChunkTV.getItems().add(creations.get(i));
				}
			});
		});
		worker.start();
	}
	
	@FXML
	private void onHomeBtnClicked(ActionEvent event) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Delete creation");
		alert.setContentText("Are you sure you want to go home? All of the progress will be lost");
		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes){
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(Main.getLayout());
			stage.setScene(scene);
		}
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
            String command = String.format("ls -a ./Creations/%s/.temp/%s* 2> /dev/null | grep -Po \".+.wav\"", _creationName, _creationName);
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

	public void setCreationData(String creationName, String searchTerm, String wikiContent) {
		_creationName = creationName;
		_searchTerm = searchTerm;
		_wikiContent = wikiContent;
		wikiTextTA.setText(_wikiContent);
	}
}
