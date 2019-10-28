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
	 * Retrieving scene appropriate for playing this creation and loading.
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
	 * Deletes this creation.
	 */
	protected void delete() {
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
	
	/**
	 * Returns the name of this creation file.
	 */
	public String getPlayableName() {
		Pattern p = Pattern.compile(".+\\/(.+)Creation.mp4$");
		Matcher m = p.matcher(_filePath);
		if (m.matches()) {
			return m.group(1);
		}
		return null;
	}
	
	/**
	 * @return the name of this creation.
	 */
	public String getCreationName() {
		return getPlayableName();
	}

	/**
	 * Placeholder method for future feature.
	 */
	protected int fetchDuration() {
		return -1;
	}
}
