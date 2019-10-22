package wikiSpeakController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.FlickrHelper;
import wikiSpeakModel.ImageGalleryEngine;
import wikiSpeakModel.ImageItem;
import wikiSpeakModel.MediaHelper;

/**
 * Controller class for controlling the finalize creation UI page and producing the creation videos.
 * 
 * @author Andrew Donovan, Xiaobin Lin
 *
 */
public class FinalizeCreationController {
	private String _searchTerm;
	private String _creationName;
	private static Map<String, String> _musicMap;
	private ImageGalleryEngine _galleryEngine;
	private String _musicTrack;
	
	@FXML private TableView<ImageItem> _imageTable;
	@FXML private TableColumn<ImageItem, ImageView> _imageCol;
	@FXML private TableColumn<ImageItem, CheckBox> _checkBoxCol;
	@FXML private Spinner<Integer> _numberImages;
	@FXML Button _createButton;
	@FXML ComboBox<String> _musicCombo;
	
	/**
	 * Set initial state of UI components
	 */
	@FXML
	private void initialize() {
		_musicMap = new LinkedHashMap<String, String>();
		_musicMap.put("None", "");
		_musicMap.put("Groove", "./res/groove.mp3");
		_musicMap.put("Melancholy", "./res/melancholy.mp3");
		_musicMap.put("Starry Night", "./res/starry.mp3");
		// List of festival voices
		for (String musicName : _musicMap.keySet()) {
			_musicCombo.getItems().add(musicName);
		}
		// Sets the combobox to select the first option by default
		_musicCombo.getSelectionModel().selectFirst();
		
		_numberImages.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
		_createButton.setDisable(true);
		_createButton.setText("Fetching images\nPlease wait...");
	}
	
	/**
	 * Sets the existing information for the creation
	 * @param creationName
	 * @param searchTerm
	 */
	public void setCreationInfo(String creationName, String searchTerm) {
		_creationName = creationName;
		_searchTerm = searchTerm;
		getImages();
	}
	
	/**
	 * Retrieve the images for this creation
	 */
	private void getImages() {
		Thread flickrWorker = new Thread(() -> {
			FlickrHelper.getImages("Creations/" + _creationName + "/", _searchTerm);
			Platform.runLater(()-> {
				try {
					initImageTable();
					_createButton.setDisable(false);
					_createButton.setText("Create!");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
		});
		flickrWorker.start();
	}
	
	/**
	 * Deletes unnecessary images so that resulting video has requested number.
	 */
	private void formatImages() {
		for (int i = _numberImages.getValue() + 1; i <=10; i++ ) {
			File imageToDelete = new File("./Creations/" + _creationName + "/.tempPhotos/" + _searchTerm + i + ".jpg");
			imageToDelete.delete();
		}
	}
	
	/**
	 * Sets the action for the home button. Returns to the main menu.
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
			Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
			stage.setScene(scene);
		}
	}
	
	/**
	 * Produces the requested creation by making various calls to a MediaHelper instance.
	 * @param event
	 */
	private void makeCreation(ActionEvent event) {
//		formatImages();
		Thread creationWorker = new Thread(() -> {			
			File audioDirectory = new File("Creations/" + _creationName + "/.temp/");
			String[] audioFiles = audioDirectory.list();
			sortAudioFiles(audioFiles);
			
			File photoDirectory = new File("Creations/" + _creationName + "/.tempPhotos");
			String[] photoFiles = photoDirectory.list();
			
			List<ImageItem> selectedImage = _galleryEngine.getSelectedImage();
			List<String> selectedImageString = new ArrayList<String>();
			
			for (ImageItem item : selectedImage) {
				String path = item.getPath();
				selectedImageString.add(path.substring(path.lastIndexOf('/')+1));
			}
			int noImages = selectedImage.size();

			String creationDir = "Creations/" + _creationName + "/";
			try {
				MediaHelper mh = new MediaHelper(creationDir);
				mh.deleteAllBut(selectedImageString);
				mh.combineAudioFiles(".temp/", audioFiles, _creationName);
				File creationAudio = new File(creationDir + ".temp/" + _creationName + ".wav");
				mh.createSlideShowToFit(noImages, creationAudio, _creationName, ".tempPhotos/", ".temp/");
				
				// Adding music if requested
				if (!_musicCombo.getValue().equals("None")) {
					String musicFile = _musicMap.get(_musicCombo.getValue());
					mh.layerAudioFiles("", musicFile, ".temp/", _creationName + ".wav", ".temp/", _creationName + "WithMusic.wav");
					mh.combineAudioVideoWithTerm(_creationName + "WithMusic", ".temp/", _creationName, ".temp/", _searchTerm, creationDir, _creationName + "Creation");
				} else {
					mh.combineAudioVideoWithTerm(_creationName, ".temp/", _creationName, ".temp/", _searchTerm, creationDir, _creationName + "Creation");
				}
				
				String command = "rm -r " + ShellHelper.WrapString(creationDir) + "/.temp " + ShellHelper.WrapString(creationDir) + "/.tempPhotos";
				ShellHelper.execute(command);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
			creationComplete(parentStage);
		});
		creationWorker.start();
	}
	
	/**
	 * Method executed to display 'creation completed' heads up to the user.
	 * @param event
	 */
	private void creationComplete(Stage parentStage) {
		// Setting code to execute when the user should be notified of creation completion
		Platform.runLater(() -> {
			Alert creationDone = new Alert(AlertType.INFORMATION);
			creationDone.setTitle("Creation Complete");
			creationDone.setContentText("Your creation, " + _creationName + ", is complete and ready for viewing.");
			creationDone.show();
									
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Main.fxml"));
			Parent layout = null;
			try {
				layout = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			loader.<MainController>getController();
			Scene scene = new Scene(layout);
			
			parentStage.setScene(scene);
		});
	}
	
	/**
	 * Sorts the audio clips into the user's order
	 * @param audioFiles
	 */
	private void sortAudioFiles(String[] audioFiles) {
		Hashtable<Integer, String> sortingFiles = new Hashtable<Integer, String>();
		for (String s: audioFiles) {
			String valString = s.substring(s.length()-5, s.length()-4);
			int val =  Integer.parseInt(valString);
			sortingFiles.put(val, s);
		}
		int count = 0;
		for (Integer i: sortingFiles.keySet()) {
			audioFiles[count] = sortingFiles.get(i);
			count ++;
		}
		Collections.reverse(Arrays.asList(audioFiles));
	}
	
	/**
	 * Sets action for create button
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void onCreate(ActionEvent e) throws IOException {	
//		// Example call:
//		List<ImageItem> selectedImage = _galleryEngine.getSelectedImage();
//		for (ImageItem image : selectedImage) {
//			String url = image.getPath();
//			String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
//			System.out.println(fileName);
//		}
		// TODO: REMOVE THE COMMENT IN THE CODE TO CREATE
		_createButton.setDisable(true);
		_createButton.setText("Creating...");
		makeCreation(e);
	}
	
	/**
	 * Load the image into the table
	 * @throws Exception - Incorrect directory for images
	 */
	private void initImageTable() throws Exception {
		_galleryEngine = new ImageGalleryEngine(new File("Creations/" + _creationName + "/" + ".tempPhotos/"));
		List<ImageItem> images = _galleryEngine.getImages();
		ObservableList<ImageItem> imagesObv = FXCollections.observableArrayList(images);
		_imageCol.setCellValueFactory(new PropertyValueFactory<ImageItem, ImageView>("ImageView"));
		_checkBoxCol.setCellValueFactory(new PropertyValueFactory<ImageItem, CheckBox>("CheckBox"));
		_imageTable.setItems(imagesObv);
	}
	
	
}
