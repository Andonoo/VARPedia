package wikiSpeakController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import wikiSpeak.FlickrHelper;
import wikiSpeak.ShellHelper;

public class FinalizeCreationController {
	private String _searchTerm;
	private String _creationName;
	@FXML
	private Spinner<Integer> _numberImages;
	
	@FXML
	private void initialize() {
		_numberImages.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
		
	}
	
	public void setCreationInfo(String creationName, String searchTerm) {
		_creationName = "Apple";
		_searchTerm = searchTerm;
		getImages();
	}
	
	private void getImages() {
		Thread flickrWorker = new Thread(() -> {
			FlickrHelper.getImages("Apple", _searchTerm);
		});
		flickrWorker.start();
	}
	
	private void formatImages() {
		for (int i = _numberImages.getValue() + 1; i <=10; i++ ) {
			File imageToDelete = new File("Creations/" + _creationName + "/.tempPhotos/" + _creationName + i + ".jpg");
			imageToDelete.delete();
		}
	}
	
	@FXML
	private void onCreate() {
		formatImages();
		String creationDir = "Creations/" + _creationName;
		Thread creationWorker = new Thread(() -> {			
			File audioDirectory = new File(creationDir + "/.temp");
			String[] audioFiles = audioDirectory.list();
			
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
			    command = "ffmpeg -framerate " + creationImageRate + " -i "+ creationDir + "/.tempPhotos/" + _creationName +"%d.jpg -vf \"pad=" + maxImageWidth + ":" + 
				maxImageHeight + "\" -r 30 " + creationDir + "/.temp/" + _creationName + ".mp4";
				ShellHelper.execute(command);
				
				command = "ffmpeg -i " + creationDir + "/.temp/" + _creationName + ".mp4 -i " + creationDir + "/.temp/" + _creationName + ".wav Creations/" +
				_creationName + "/" + _creationName + "Creation.mp4";
				ShellHelper.execute(command);
				
				command = "rm -r " + creationDir + "/.temp";
				ShellHelper.execute(command);
				
				command = "rm -r " + creationDir + "/.tempPhotos";
				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		creationWorker.start();
	}
}
