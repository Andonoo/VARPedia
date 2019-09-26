package wikiSpeak;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class AudioChunk extends Playable{

	public AudioChunk(String playableName, Runnable afterDelete) {
		super(playableName, afterDelete);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPlay(ActionEvent event) {
		Thread worker = new Thread(()->{
			try {
				String command = String.format("play %s.wav", _playableName);
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
						String command = String.format("rm %s.*", _playableName);
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

}
