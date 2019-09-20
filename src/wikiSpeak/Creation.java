package wikiSpeak;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/***
 * A class to represent an existing creation
 * @author Xiaobin Lin
 *
 */
public class Creation {
	private String _creationName;
	private String _duration;
	private Button _play;
	private Button _delete;
	private Runnable _afterDelete;
	
	public Creation(String creationName, Runnable afterDelete){
		_creationName = creationName;
		_afterDelete = afterDelete;
		try {
			// Get the duration to a whole number string
			String command = String.format("ffprobe -i ./Creations/\"%s.mp4\" -show_format -v " +
					"quiet | sed -n 's/duration=//p'", creationName);
			_duration = String.valueOf((int) Double.parseDouble(ShellHelper.execute(command).get(0)));
		} catch (Exception e) {
			_duration = null;
		}
		_play = new Button("play");
		_play.setOnAction(event -> onPlay());
		_delete = new Button("delete");
		_delete.setOnAction(event -> onDelete());
    }
	
	/***
	 * Play the creation using ffplay on a different thread
	 */
	private void onPlay() {
			Thread worker = new Thread(()->{
			try {
				String command = String.format("ffplay -autoexit ./Creations/%s.mp4", _creationName);
				ShellHelper.execute(command);
			} catch (Exception e) {
				e.printStackTrace();
			}});
			worker.start();
	}
	
	/***
	 * Confirm with user then delete the creation
	 */
	private void onDelete() {
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
					String command = String.format("rm ./Creations/%s.mp4", _creationName);
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
	
	public String getCreationName() {
		return _creationName;
	}
	
	public String getDuration() {
		return _duration;
	}
	
	public Button getPlay() {
		return _play;
	}
	
	public Button getDelete() {
		return _delete;
	}
}
