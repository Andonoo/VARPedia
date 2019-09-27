package wikiSpeakController;

import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeak.Search;
import wikiSpeak.ShellHelper;
import wikiSpeak.ViewCreations;

public class SearchController {
	public static final String contentPlaceHolder = "Please use the input field above to search";
	public static final String loadingText = "Loading...";
	private String _searchTerm;
	private String _creationName;
	
	@FXML
	private TextField searchTF;
	
	@FXML
	private TextArea searchResultTF;
	
	@FXML
	private TextField creationNameTF;
	
	@FXML
	private Button nextBtn;
	
	@FXML
    public void initialize() {
        searchResultTF.setEditable(false);
//        nextBtn.setDisable(true);
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
	
	@FXML
	private void onNextBtnClicked(ActionEvent event) throws IOException {
		String wikiText = searchResultTF.getText();
		String creationName = creationNameTF.getText();
		
		try {
			makeCreationFolder(creationName);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Creation already exists, please try another name.");
			alert.showAndWait();
			return;
		}
		
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("CreateAudio.fxml"));
		Parent layout = loader.load();
		
		CreateAudioController controller = loader.<CreateAudioController>getController();
		controller.setCreationData(_creationName, _searchTerm, wikiText);
		Scene scene = new Scene(layout);
		
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}
	
	@FXML
	private void onSearchBtnClicked(ActionEvent event) throws IOException {
		searchResultTF.setPromptText(loadingText);
		Thread worker = new Thread(() -> {
			try{
				String command = String.format("wikit %s", searchTF.getText());
				List<String> output = ShellHelper.execute(command);
				if (output.size() == 0 || output.get(0).contains("not found :^(")) {
					// Show alert and exit
					Platform.runLater(() -> {
						searchResultTF.setPromptText(contentPlaceHolder);						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText(String.format("%s: Nothing found :(", searchTF.getText()));
						resetCreate();
						alert.showAndWait();
					});
					return;
				}
				String wikiText = output.get(0);
				_searchTerm = searchTF.getText();

				Platform.runLater(() -> {
					// Update wikitContents table to show wikit text, remove the first two spaces
					searchResultTF.setText(wikiText.substring(2));
					nextBtn.setDisable(false);
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					searchResultTF.setPromptText(contentPlaceHolder);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Invalid search, please search with a different word");
					alert.showAndWait();
				});
			}
		});
		worker.start();
	}
	
	private void makeCreationFolder(String name) throws Exception {
			ShellHelper.execute("mkdir ./Creations/" + name);
	}
	
	private void resetCreate() {
		searchResultTF.clear();
		nextBtn.setDisable(true);
	}
}
