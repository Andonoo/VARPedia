package wikiSpeakModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import wikiSpeakController.ShellHelper;

/**
 * A class to represent a Audio Chunk audio file
 * 
 * @author Xiaobin Lin
 *
 */
public class AudioChunk extends Playable{
	/**
	 * Produces an audio chunk with the provided playable. 
	 * 
	 * @param playableName
	 * @param afterDelete called when the chunk will be deleted
	 */
	public AudioChunk(String playableName, Runnable afterDelete) {
		super(playableName, afterDelete);
	}
	
	/**
	 * Plays this audio chunk.
	 */
	@Override
	protected void onPlay(ActionEvent event) {
		Thread worker = new Thread(()->{
			try {
				String command = String.format("play %s", ShellHelper.WrapString(_filePath));
				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		worker.start();
	}

	/**
	 * Deletes this audio chunk.
	 */
	@Override
	protected void delete() {
		Thread worker = new Thread(()->{
		try {
			String command = String.format("rm %s", ShellHelper.WrapString(_filePath));
			ShellHelper.execute(command);
			// Run the action specified by client, mainly for refreshing the UI
			Platform.runLater(()-> _afterDelete.run());
		} catch (Exception e) {
			Platform.runLater(()->{
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setContentText("Unable to delete " + e.getMessage());
				errorAlert.showAndWait();
			});
		}});
		worker.start();
	}

	/**
	 * Get the name of the playable e.g. ./apple.wav -> apple
	 */
	public String getPlayableName() {
		Pattern p = Pattern.compile(".+\\/(.+).wav$");
		Matcher m = p.matcher(_filePath);
		if (m.matches()) {
			return m.group(1);
		}
		return null;
	}
	
	/**
	 * Returns the duration of this audio chunk.
	 */
	@Override
	protected int fetchDuration() {
		try {
			// Get the duration to a whole number string
			String command = String.format("ffprobe -i \"%s\" -show_format -v " +
					"quiet | sed -n 's/duration=//p'", _filePath);
			return((int) Double.parseDouble(ShellHelper.execute(command).get(0)));
		} catch (Exception e) {
			return 0;
		}
	}

}
