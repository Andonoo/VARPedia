package wikiSpeakController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

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
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import wikiSpeak.FlickrHelper;
import wikiSpeak.Main;
import wikiSpeak.ShellHelper;

public class FinalizeCreationController {
	private String _searchTerm;
	private String _creationName;
	@FXML
	private Spinner<Integer> _numberImages;
	
	@FXML Button _createButton;
	
	@FXML
	private void initialize() {
		_numberImages.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
		_createButton.setDisable(true);
		_createButton.setText("Fetching images\nPlease wait...");
	}
	
	public void setCreationInfo(String creationName, String searchTerm) {
		_creationName = creationName;
		_searchTerm = searchTerm;
		getImages();
	}
	
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
	
	private void formatImages() {
		for (int i = _numberImages.getValue() + 1; i <=10; i++ ) {
			File imageToDelete = new File("Creations/" + _creationName + "/.tempPhotos/" + _creationName + i + ".jpg");
			imageToDelete.delete();
		}
	}
	
	private void makeCreation() {
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
				String command = "sox ";
				for (String s: audioFiles) {
					command = command + creationDir + "/.temp/" + s + " ";
				}
				command = command + creationDir + "/.temp/" + _creationName + ".wav 2> /dev/null";
				ShellHelper.execute(command);
				
				File creationAudio = new File(creationDir + "/.temp/" + _creationName + ".wav");
				AudioInputStream creationAudioStream = AudioSystem.getAudioInputStream(creationAudio);
				AudioFormat creationAudioFormat = creationAudioStream.getFormat();
			    long audioFileLength = creationAudio.length();
			    int frameSize = creationAudioFormat.getFrameSize();
			    float frameRate = creationAudioFormat.getFrameRate();
			    float creationAudioDuration = (audioFileLength / (frameSize * frameRate));
				float creationImageRate = noImages/creationAudioDuration;
			    command = "ffmpeg -framerate " + creationImageRate + " -i "+ creationDir + "/.tempPhotos/" + _searchTerm + 
			    		"%d.jpg -vf scale=800x400 " + "-r 30 " + creationDir + "/.temp/" + _creationName + ".mp4";
				ShellHelper.execute(command);
				
				command = "ffmpeg -i " + creationDir + "/.temp/" + _creationName + ".mp4 -i " + creationDir + "/.temp/" + _creationName + ".wav -vf "
						+ "\"drawtext=fontfile=./BodoniFLF-Roman.ttf:fontsize=100:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + _searchTerm + "\" Creations/" +
				_creationName + "/" + _creationName + "Creation.mp4";
				ShellHelper.execute(command);
				
				command = "rm -r " + creationDir + "/.temp";
				ShellHelper.execute(command);
				
				command = "rm -r " + creationDir + "/.tempPhotos";
				ShellHelper.execute(command);
				
				command = "rm " + creationDir + "/.temp.txt";
				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				Alert creationDone = new Alert(AlertType.INFORMATION);
				creationDone.setTitle("Creation Complete");
				creationDone.setContentText("Your creation, " + _creationName + ", is complete and ready for viewing.");
				creationDone.show();
			});
		});
		creationWorker.start();
	}
	
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

	@FXML
	private void onCreate(ActionEvent e) throws IOException {
		makeCreation();
		
		Stage parentStage = (Stage)((Node) e.getSource()).getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("Main.fxml"));
		Parent layout = loader.load();
		
		MainController controller = loader.<MainController>getController();
		Scene scene = new Scene(layout);
		
		parentStage.setScene(scene);
	}
}
