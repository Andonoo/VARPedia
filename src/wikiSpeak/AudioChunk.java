package wikiSpeak;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * A class to represent a Audio Chunk audio file
 * @author Xiaobin Lin
 *
 */
public class AudioChunk extends Playable{
	public AudioChunk(String playableName, Runnable afterDelete) {
		super(playableName, afterDelete);
	}

	@Override
	protected void onPlay(ActionEvent event) {
		Thread worker = new Thread(()->{
			try {
				String command = String.format("play %s", _filePath);
				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		worker.start();
	}

	@Override
	protected void onDelete(ActionEvent event) {
		// Make alert to confirm with user
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Delete creation");
			alert.setContentText("Are you sure you want to delete?");
			ButtonType buttonTypeYes = new ButtonType("Yes");
			ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeYes){
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
