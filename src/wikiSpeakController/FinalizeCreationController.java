package wikiSpeakController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javafx.application.Platform;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import wikiSpeak.FlickrHelper;
import wikiSpeak.Main;
import wikiSpeak.ShellHelper;

/**
 * Controller class for controlling the finalize creation UI page and producing the creation videos.
 * 
 * @author Andrew Donovan
 *
 */
public class FinalizeCreationController {
	private String _searchTerm;
	private String _creationName;
	@FXML
	private Spinner<Integer> _numberImages;
	
	@FXML Button _createButton;
	
	/**
	 * Set initial state of UI components
	 */
	@FXML
	private void initialize() {
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
			FlickrHelper.getImages(_creationName, _searchTerm);
			Platform.runLater(()-> {
				_createButton.setDisable(false);
				_createButton.setText("Create!");
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
		// Special case when n = 2 to circumvent ffmpeg
		if (_numberImages.getValue() == 2) {
			File image = new File("./Creations/" + _creationName + "/.tempPhotos/" + _searchTerm + "2" + ".jpg");
			File newImage = new File("./Creations/" + _creationName + "/.tempPhotos/" + _searchTerm + "3" + ".jpg");
			try {
				Files.copy(image.toPath(), newImage.toPath());
			} catch (IOException e) {
				return;
			}
			
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
			Scene scene = new Scene(Main.getLayout());
			stage.setScene(scene);
		}
	}
	
	/**
	 * Produces the requested creation
	 * @param event
	 */
	private void makeCreation(ActionEvent event) {
		formatImages();
		String creationDir = "Creations/" + _creationName;
		Thread creationWorker = new Thread(() -> {			
			File audioDirectory = new File(creationDir + "/.temp/");
			String[] audioFiles = audioDirectory.list();
			sortAudioFiles(audioFiles);
			
			File photoDirectory = new File(creationDir + "/.tempPhotos");
			String[] photoFiles = photoDirectory.list();
			
			int maxImageWidth = 0;
			int maxImageHeight = 0;
			float noImages = _numberImages.getValue();
			
			// Determines maximum dimensions from downloaded photos
			for (String p: photoFiles) {
				BufferedImage image;
				try {
					image = ImageIO.read(new File(creationDir + "/.tempPhotos/" + p));
					int width = image.getWidth();
					int height = image.getHeight();
					if (width > maxImageWidth) {
						maxImageWidth = width;
					}
					if (height > maxImageHeight) {
						maxImageHeight = height;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				// Creating audio file
				String command = "sox ";
				for (String s: audioFiles) {
					command = command + creationDir + "/.temp/" + s + " ";
				}
				command = command + creationDir + "/.temp/" + _creationName + ".wav 2> /dev/null";
				ShellHelper.execute(command);
				
				// Creating slide show
				File creationAudio = new File(creationDir + "/.temp/" + _creationName + ".wav");
				AudioInputStream creationAudioStream = AudioSystem.getAudioInputStream(creationAudio);
				AudioFormat creationAudioFormat = creationAudioStream.getFormat();
			    long audioFileLength = creationAudio.length();
			    int frameSize = creationAudioFormat.getFrameSize();
			    float frameRate = creationAudioFormat.getFrameRate();
			    float creationAudioDuration = (audioFileLength / (frameSize * frameRate));
				float creationImageRate = noImages/creationAudioDuration;
				
				if (noImages == 1) {
					command = "ffmpeg -loop 1 -i " + creationDir + "/.tempPhotos/" + _searchTerm + 
				    		"1.jpg -t " + creationAudioDuration + " -vf scale=320x240 " + creationDir + "/.temp/" + _creationName + ".mp4";
					ShellHelper.execute(command);
				} else if (noImages == 2){
					// Combining audio and slide show
				    command = "ffmpeg -framerate " + creationImageRate + " -i "+ creationDir + "/.tempPhotos/" + _searchTerm + 
				    		"%d.jpg -vf scale=320x240 " + "-r 30 " + creationDir + "/.temp/" + _creationName + ".mp4";
					ShellHelper.execute(command);
				} else {
					// Combining audio and slide show
				    command = "ffmpeg -framerate " + creationImageRate + " -i "+ creationDir + "/.tempPhotos/" + _searchTerm + 
				    		"%d.jpg -vf scale=320x240 " + "-r 30 " + creationDir + "/.temp/" + _creationName + ".mp4";
					ShellHelper.execute(command);
				}
				
					// Combining audio and video with text
					command = "ffmpeg -i " + creationDir + "/.temp/" + _creationName + ".mp4 -i " + creationDir + "/.temp/" + _creationName + ".wav -vf "
							+ "\"drawtext=fontfile=./BodoniFLF-Roman.ttf:fontsize=100:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + _searchTerm + "\" Creations/" +
					_creationName + "/" + _creationName + "Creation.mp4";
					ShellHelper.execute(command);

				// Removing temp
//				command = "rm -r " + creationDir + "/.temp " + creationDir + "/.tempPhotos " + creationDir+ "/.temp.txt";
//				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Setting code to execute when the user should be notified of creation completion
			Platform.runLater(() -> {
				Alert creationDone = new Alert(AlertType.INFORMATION);
				creationDone.setTitle("Creation Complete");
				creationDone.setContentText("Your creation, " + _creationName + ", is complete and ready for viewing.");
				creationDone.show();
				
				Stage parentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
				
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
		});
		creationWorker.start();
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
		_createButton.setDisable(true);
		_createButton.setText("Creating...");
		makeCreation(e);
	}
}
