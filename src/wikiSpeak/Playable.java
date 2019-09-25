package wikiSpeak;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public abstract class Playable {
	protected String _playableName;
	protected String _duration;
	protected Button _play;
	protected Button _delete;
	protected Runnable _afterDelete;
	
	public Playable(String playableName, Runnable afterDelete){
		_playableName = playableName;
		_afterDelete = afterDelete;
		try {
			// Get the duration to a whole number string
			String command = String.format("ffprobe -i ./Creations/\"%s.mp4\" -show_format -v " +
					"quiet | sed -n 's/duration=//p'", playableName);
			_duration = String.valueOf((int) Double.parseDouble(ShellHelper.execute(command).get(0)));
		} catch (Exception e) {
			_duration = null;
		}
		_play = new Button("play");
		_play.setOnAction(event -> onPlay(event));
		_delete = new Button("delete");
		_delete.setOnAction(event -> onDelete(event));
    }

	protected abstract void onPlay(ActionEvent event);
	
	protected abstract void onDelete(ActionEvent event);
	
	public String getName() {
		return _playableName;
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
