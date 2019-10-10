package wikiSpeakModel;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

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
		
		_play = new Button("play");
		_play.setOnAction(event -> onPlay(event));
		_delete = new Button("delete");
		_delete.setOnAction(event -> onDelete(event));
		
		File file = new File(filePath);
		_directoryPath = file.getParent();
    }
	
	protected abstract int fetchDuration();

	protected abstract void onPlay(ActionEvent event);
	
	protected abstract void onDelete(ActionEvent event);
	
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
