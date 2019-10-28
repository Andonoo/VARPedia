package wikiSpeakModel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import wikiSpeakController.ShellHelper;

/**
 * Class to handle the retrieval, creation, playing and storing of media components.
 * @author Andrew Donovan
 *
 */
public class MediaHelper {
	private String _workingDir;

	/**
	 * @param workingDir in the form "directory/"
	 */
	public MediaHelper(String workingDir) {
		_workingDir = workingDir;
		createFolder();
	}
	
		
	/**
	 * Uses text to speech to turn the provided string into an audio file and saves it in
	 * the provided location.
	 * @param text to be converted to audio
	 * @param destDir directory where chunk should be saved
	 * @param voice to determine how audio sounds
	 * @param chunkName name of generated audio file
	 * @throws Exception
	 */
	public void createAudioChunk(String text, String voice, String chunkName) throws Exception {
		text = cleanText(text);
		// Save the text file and save it to provided location
		String command = String.format("echo \"%s\" > %s", text, ShellHelper.WrapString(_workingDir + ".temp.txt"));
		ShellHelper.execute(command);
		// Turn the text file into an audio chunk
		command = String.format("text2wave -o %s -eval \'(voice_%s)\' < %s",
				ShellHelper.WrapString(_workingDir + chunkName + ".wav"), voice, ShellHelper.WrapString(_workingDir + ".temp.txt"));
		ShellHelper.execute(command);
		// Deleting .temp.txt text file
		deleteFile(_workingDir + ".temp.txt");
	}
	
	/**
	 * Search wikipedia using wikit and return result
	 * @param term
	 * @return wiki information
	 * @throws Exception
	 */
	public static String searchWiki(String term) throws Exception {
		// Search wikipedia using wikit
		String command = String.format("wikit %s", term);
		List<String> output = ShellHelper.execute(command);
		
		// Error check from wikit
		if (output.size() == 0 || output.get(0).contains("not found :^(")) {
			throw new Exception("Nothing found");
		}
		// Return the string with the first two whitespace removed
		return output.get(0).substring(2);
	}
	
	/**
	 * Clean text so festival could read it.
	 * @return
	 */
	private static String cleanText(String text) {
		// Filter out all special character that the text synthesizer can't speak
		text = text.replaceAll("[^a-zA-Z\\s\\,\\.0-9\\-\\']", " ");
		return text;
	}
	
	/**
	 * Uses text to speech to play a preview of the provided text in the provided voice option.
	 * Destination is where temp files will be located while playing (deleted afterwards).
	 * @param text to be converted to audio
	 * @param destDir directory of temp files
	 * @param voice to determine how audio sounds
	 * @throws Exception
	 */
	public void previewAudioChunk(String text, String voice) throws Exception {
		text = cleanText(text);
		// Create a wav file using text2wave
		String command = String.format("echo \"%s\" > %s.temp.txt", text, _workingDir);
		ShellHelper.execute(command);
		command = String.format("text2wave -o %s.temp.wav -eval \'(voice_%s)\' < %s.temp.txt",
				_workingDir, voice, _workingDir);
		ShellHelper.execute(command);
		command = String.format("play %s.temp.wav", _workingDir);
		ShellHelper.execute(command);
		command = String.format("rm %s.temp.txt %s.temp.wav", _workingDir, _workingDir);
		ShellHelper.execute(command);
	}
	
	/**
	 * Gets the provided audio files and combines them into one depending on order of array. The audio file
	 * will be output in the audioDir.
	 * @param audioDir
	 * @param audioFiles
	 * @param creationName
	 * @throws Exception
	 */
	public void combineAudioFiles(String audioDir, String[] audioFiles, String creationName, String outputDir) throws Exception {
		// Creating audio file
		String command = "sox ";
		for (String s: audioFiles) {
			command = command + ShellHelper.WrapString(_workingDir + audioDir + s) + " ";
		}
		command = command +  ShellHelper.WrapString(_workingDir + outputDir + creationName) + ".wav 2> /dev/null";
		ShellHelper.execute(command);
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
	 * Returns the maximum height and width from the provided images as an array of two integers.
	 * @param dir Directory containing images
	 * @param photoFiles names of image files
	 * @return max width (int[0]) and height(int[1])
	 */
	private int[] getMaxImageDimensions(String dir, String[] photoFiles) {
		int maxImageWidth = 0;
		int maxImageHeight = 0;
		for (String p: photoFiles) {
			BufferedImage image;
			try {
				image = ImageIO.read(new File(dir + "/" + p));
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
		int[] result = {maxImageWidth, maxImageHeight};
		return result;
	}
	
	/**
	 * Takes in a list of filenames and delete everything but those files
	 * @param filenames
	 * @throws Exception
	 */
	public void deleteAllBut(List<String> filenames) throws Exception {
		File fileFolder = new File(String.format("./%s/.tempPhotos/", _workingDir));
		for (File file : fileFolder.listFiles()) {
			if (!filenames.contains(file.getName())) {
				file.delete();
			}
		}
	}
	
	/**
	 * Takes the images at the provided image directory and turns them into a slideshow to fit the length
	 * of the audio file provided. Video will be created in specified destination path.
	 * @param noImages number of images
	 * @param audioFile
	 * @param videoName of output video
	 * @param destPath path from working directory where output video will be created
	 * @throws Exception
	 */
	public void createSlideShowToFit(int noImages, File audioFile, String videoName, String imageDir, String destPath) throws Exception {
		AudioInputStream creationAudioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat creationAudioFormat = creationAudioStream.getFormat();
	    long audioFileLength = audioFile.length();
	    int frameSize = creationAudioFormat.getFrameSize();
	    float frameRate = creationAudioFormat.getFrameRate();
	    float creationAudioDuration = (audioFileLength / (frameSize * frameRate));
		float creationImageRate = noImages/creationAudioDuration;
		
		String command = "cat " + ShellHelper.WrapString(_workingDir + imageDir) + "*.jpg | ffmpeg -f image2pipe -framerate " + creationImageRate + " "
				+ "-i - -c:v libx264 -pix_fmt yuv420p -vf \"scale=480x360\" -r 25 -max_muxing_queue_size 1024 " + ShellHelper.WrapString(_workingDir + destPath + videoName) + ".mp4";
		ShellHelper.execute(command);
	}
	
	/**
	 * Takes the images at the provided image directory and turns them into a slideshow to fit the length
	 * provided. Video will be created in specified destination path.
	 * @param noImages number of images
	 * @param length
	 * @param videoName of output video
	 * @param destPath path from working directory where output video will be created
	 * @throws Exception
	 */
	public void createSlideShowToLength(int noImages, int length, String videoName, String imageDir, String destPath) throws Exception {
		int imageRate = noImages/length;
		String command = "cat " + ShellHelper.WrapString(_workingDir + imageDir) + "*.jpg | ffmpeg -f image2pipe -framerate " + imageRate + " "
				+ "-i - -c:v libx264 -pix_fmt yuv420p -vf \"scale=320x240\" -r 25 -max_muxing_queue_size 1024 " + ShellHelper.WrapString(_workingDir + destPath + videoName) + ".mp4";
		ShellHelper.execute(command);
	}
	
	/**
	 * Takes audio file and combines with video file with overlaid text. 
	 * @param audioFile
	 * @param audioDir
	 * @param videoFile
	 * @param videoDir
	 * @param term
	 * @param outputDir
	 * @param outputName
	 * @throws Exception
	 */
	public void combineAudioVideoWithTerm(String audioFile, String audioDir, String videoFile, String videoDir, String term, String outputDir, String outputName) throws Exception {
		String command = "ffmpeg -i " + ShellHelper.WrapString(_workingDir + videoDir + videoFile) + ".mp4 -i " + ShellHelper.WrapString(_workingDir + audioDir + audioFile) + ".wav -vf "
				+ "\"drawtext=fontfile=./BodoniFLF-Roman.ttf:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + term + "\" "
				+ ShellHelper.WrapString(outputDir + outputName) + ".mp4";
		ShellHelper.execute(command);
	}
	
	/**
	 * Takes the provided audio file and loops it to match the other audio file's length.
	 * @param audioFile
	 * @param audioDir
	 * @param outputDir
	 * @param outputName
	 */
	public void layerAudioFiles(String audioDir, String audioFile, String audioDirToFit, String audioFileToFit, String outputDir, String outputName) {
		File aFile = new File(audioDir + audioFile);
	    long audioFileLength = aFile.length();
	    File afFile = new File(_workingDir + audioDirToFit + audioFileToFit);
	    long audioToFitFileLength = afFile.length();
	    int loopsRequired = (int) (audioToFitFileLength/audioFileLength + 2);
	    	    
		try {
			String command = String.format("ffmpeg -stream_loop %s -i %s %s", loopsRequired, ShellHelper.WrapString(audioDir + audioFile), ShellHelper.WrapString(_workingDir + audioDirToFit + "LoopedAudio.mp3"));
			ShellHelper.execute(command);
			command = String.format("ffmpeg -i %s -i %s -filter_complex amerge %s", ShellHelper.WrapString(_workingDir + audioDirToFit + "LoopedAudio.mp3"), 
					ShellHelper.WrapString(_workingDir + audioDirToFit + audioFileToFit), ShellHelper.WrapString(_workingDir + outputDir + outputName));
			ShellHelper.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the file at the provided location
	 * @param file
	 * @throws Exception
	 */
	private static void deleteFile(String file) throws Exception {
		String command = "rm " + ShellHelper.WrapString(file);
		ShellHelper.execute(command);
	}
	
	/**
	 * Play audio using bash command "play"
	 * @param filePath
	 * @throws Exception
	 */
	public static void playAudio(String filePath) throws Exception {
		String command = String.format("play %s", filePath);
		ShellHelper.execute(command);
	}
	

	/**
	 * Create folder if it doesn't exist already
	 */
	private void createFolder() {
		String command = String.format("mkdir -p %s", ShellHelper.WrapString(_workingDir));
		try {
			ShellHelper.execute(command);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
