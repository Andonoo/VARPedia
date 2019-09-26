package wikiSpeak;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

/***
 * A class to represent an existing creation
 * @author Xiaobin Lin, Andrew Donovan
 *
 */
public class Creation extends Playable{
	private Runnable _afterDelete;
	
	public Creation(String creationName, Runnable afterDelete){
		super(creationName, afterDelete);
    }
	
	/***
	 * Retrieving scene appropriate for playing this creation and loading
	 */
	protected void onPlay(ActionEvent event) {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		try {
			Scene scene = new Scene(PlayerUI.getLayout(_playableName));
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Confirm with user then delete the creation
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
				Thread worker = new Thread(()->{
				try {
					String command = String.format("rm ./Creations/%s.*", _playableName);
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
		return _playableName;
	}
}
