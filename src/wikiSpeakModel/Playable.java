package wikiSpeakModel;

import java.io.File;
import java.util.Optional;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * Class to define a playable media object, as used for display in a table.
 */
public abstract class Playable {
	protected String _directoryPath;
	protected String _filePath;
	protected String _playableName;
	protected String _duration;
	protected Button _play;
	protected Button _delete;
	protected Runnable _afterDelete;
	
	/**
	 * Creates a media object for the provided pathname. 
	 * @param filePath
	 * @param afterDelete executed on deletion of this element
	 */
	public Playable(String filePath, Runnable afterDelete){
		_filePath = filePath;
		_afterDelete = afterDelete;
		_playableName = getPlayableName();
		_duration = String.valueOf(fetchDuration());
		
		_play = new JFXButton("Play");
		_play.setOnAction(event -> onPlay(event));
		_play.setPrefWidth(150);
		_play.setStyle("-fx-background-color:#aed581");
		
		_delete = new JFXButton("Delete");
		_delete.setOnAction(event -> onDelete(event));
		_delete.setPrefWidth(150);
		_delete.setStyle("-fx-background-color:#e57373");
		
		File file = new File(filePath);
		_directoryPath = file.getParent();
    }
	
	/**
	 * @return duration of this media element.
	 */
	protected abstract int fetchDuration();

	/**
	 * Plays this playable.
	 * @param event
	 */
	protected abstract void onPlay(ActionEvent event);
	
	/**
	 * Requests confirmation from users and if it is given, deletes this playable.
	 * @param event
	 */
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
			this.delete();
		}
	};
	
	/**
	 * Deletes this media element.
	 */
	protected abstract void delete();
	
	/**
	 * @return the file name of this playable.
	 */
	public abstract String getPlayableName();

	/**
	 * @return the path to this playable file.
	 */
	public String getPath() {
		return _filePath;
	}
	
	/**
	 * @return the duration of this playable.
	 */
	public String getDuration() {
		return _duration;
	}
	
	/**
	 * @return a button to play this playable from a table
	 */
	public Button getPlay() {
		return _play;
	}
	
	/**
	 * @return a button to delete this playable from a table
	 */
	public Button getDelete() {
		return _delete;
	}
	
}
