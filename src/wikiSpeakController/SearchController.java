package wikiSpeakController;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeakModel.MediaHelper;

/**
 * Controller class for text search UI component.
 * 
 * @author Andrew Donovan, Xiaobin Lin
 */
public class SearchController extends Navigation{
	public static final String contentPlaceHolder = "Please use the input field above to search";
	public static final String loadingText = "Loading...";
	private String _searchTerm;
	private String _creationName;

	@FXML private TextField _searchTF;
	@FXML private TextArea _searchResultTF;
	@FXML private TextField _creationNameTF;
	@FXML private Button _nextBtn;

	/**
	 * Method called on loading of term search scene.
	 */
	@FXML
	public void initialize() {
		_searchResultTF.setEditable(false);
		_nextBtn.setDisable(true);
	}

	/**
	 * Do all the checks and attempt to go to the next step of the wizard
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onNextBtnClicked(ActionEvent event) throws IOException {
		String wikiText = _searchResultTF.getText();
		_creationName = _creationNameTF.getText();

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
		
		// Saves media components for use in the quiz.
		saveUsersSearchText(wikiText);
		saveUsersSearchTerm();
		
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("CreateAudio.fxml"));
		Parent layout = loader.load();

		CreateAudioController controller = loader.<CreateAudioController>getController();
		controller.setCreationData(_creationName, _searchTerm, wikiText);
		Scene scene = new Scene(layout);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}
	
	/**
	 * Saves the user's search text for the purpose of future use in the quiz.
	 * 
	 * @param wikiText
	 * @throws IOException
	 */
	private void saveUsersSearchText(String wikiText) throws IOException {
		File creationTxt = new File(String.format("Creations/%s/%s.txt", _creationName, _creationName));
		creationTxt.createNewFile();
		PrintWriter output = new PrintWriter(creationTxt);
		output.println(wikiText);
		output.close();
	}
	
	/**
	 * Saves the user's search term for the purpose of future use in the quiz.
	 */
	private void saveUsersSearchTerm() {
		try {
			String command = String.format("mkdir Creations/%s/Term", ShellHelper.WrapString(_creationName));
			ShellHelper.execute(command);
			command = String.format("echo '' > Creations/%s/Term/%s", ShellHelper.WrapString(_creationName), ShellHelper.WrapString(_searchTerm));
			ShellHelper.execute(command);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Fetch content using wikit
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onSearchBtnClicked(ActionEvent event) throws IOException {
		_searchResultTF.clear();
		_searchResultTF.setPromptText(loadingText);
		Thread worker = new Thread(() -> {
			try {
				String wikiText = MediaHelper.searchWiki(_searchTF.getText());
				_searchTerm = _searchTF.getText();

				Platform.runLater(() -> {
					// Update wikitContents table to show wikit text, remove the first two spaces
					_searchResultTF.setText(wikiText);
					_nextBtn.setDisable(false);
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					_searchResultTF.setPromptText(contentPlaceHolder);
					showAlert("Invalid search, please search with a different word");
				});
			}
		});
		worker.start();
	}

	/**
	 * Create the needed folders for temp creations
	 * 
	 * @param name
	 * @throws Exception
	 */
	private void makeCreationFolder(String name) throws Exception {
		ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name));
		ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name) + "/.temp");
		ShellHelper.execute("mkdir ./Creations/" + ShellHelper.WrapString(name) + "/.tempPhotos");
	}
}
