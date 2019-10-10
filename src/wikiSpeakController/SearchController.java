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
import wikiSpeak.ShellHelper;

/**
 * Controller class for text search UI component.
 *
 */
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
        nextBtn.setDisable(true);
    }
	
	/**
	 * Show an alert dialog with actionable buttons
	 * @param event
	 * @throws IOException
	 */
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
	
	/**
	 * Do all the checks and attempt to go to the next step of the wizard
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onNextBtnClicked(ActionEvent event) throws IOException {
		String wikiText = searchResultTF.getText();
		_creationName = creationNameTF.getText();
		
		if (_creationName.length() <= 0) {
			showAlert("Creation name must be entered");
			return;
		}
		
		try {
			makeCreationFolder(_creationName);
		} catch (Exception e) {
			showAlert("Creation already exists, please try another name.");
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
	
	/**
	 * Fetch content using wikit
	 * @param event
	 * @throws IOException
	 */
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
						String message = String.format("%s: Nothing found :(", searchTF.getText());
						showAlert(message);
						resetCreate();
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
					showAlert("Invalid search, please search with a different word");
				});
			}
		});
		worker.start();
	}
	
	/**
	 * Create the needed folders for temp creations
	 * @param name
	 * @throws Exception
	 */
	private void makeCreationFolder(String name) throws Exception {
			ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name) );
			ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name) + "/.temp");
			ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name) + "/.tempPhotos");
	}
	
	/**
	 * Reset creation to allow user to start again
	 */
	private void resetCreate() {
		searchResultTF.clear();
		nextBtn.setDisable(true);
	}
	
	/**
	 * Show alert dialog
	 * @param message
	 */
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
}
