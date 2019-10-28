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
 * Class to define a playable media object.
 */
public abstract class Playable {
	protected String _directoryPath;
	protected String _filePath;
	protected String _playableName;
	protected String _duration;
	protected Button _play;
	protected Button _delete;
	protected Runnable _afterDelete;
	
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
	
	protected abstract int fetchDuration();

	protected abstract void onPlay(ActionEvent event);
	
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
	
	protected abstract void delete();
	
	public abstract String getPlayableName();

	public String getPath() {
		return _filePath;
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
