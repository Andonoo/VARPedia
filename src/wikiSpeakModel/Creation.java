package wikiSpeakModel;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher;
import wikiSpeakController.ShellHelper;
import javafx.scene.control.ButtonType;

/***
 * A class to represent an existing creation
 * @author Xiaobin Lin, Andrew Donovan
 *
 */
public class Creation extends Playable{
	public Creation(String creationName, Runnable afterDelete){
		super(creationName, afterDelete);
    }
	
	/***
	 * Retrieving scene appropriate for playing this creation and loading
	 */
	protected void onPlay(ActionEvent event) {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		try {
			Scene scene = new Scene(SceneSwitcher.getPlayerLayout(this));
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
					String command = String.format("rm -r %s", ShellHelper.WrapString(_directoryPath));
					ShellHelper.execute(command);
					// Run the action specified by client, mainly for refreshing the UI
					Platform.runLater(_afterDelete);
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
	
	public String getPlayableName() {
		Pattern p = Pattern.compile(".+\\/(.+)Creation.mp4$");
		Matcher m = p.matcher(_filePath);
		if (m.matches()) {
			return m.group(1);
		}
		return null;
	}
	
	public String getCreationName() {
		return getPlayableName();
	}

	protected int fetchDuration() {
		return -1;
		// The following code fetches the creation duration, it is a time consuming process so 
		// only use when needed
//		try {
//			// Get the duration to a whole number string
//			String command = String.format("ffprobe -i \"%s\" -show_format -v " +
//					"quiet | sed -n 's/duration=//p'", _filePath);
//			return((int) Double.parseDouble(ShellHelper.execute(command).get(0)));
//		} catch (Exception e) {
//			return 0;
//		}
	}
}
