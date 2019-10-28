package wikiSpeak;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wikiSpeakController.SceneSwitcher;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakController.ShellHelper;

/**
 * Main application method for VARpedia
 * @author Andrew Donovan, Xiaobin Lin
 * 
 */
public class Main extends Application{	
	// Entries to this array will be deleted on closing of the application.
	private static String[] tempFiles = {
		".Game/.Round*",
	};
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
		primaryStage.setScene(scene);
		primaryStage.setTitle("VARPedia");
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		    	onCloseCleanup();
		    }
		});
	}
	
	/**
	 * Iterates through list of declared temp files. If any have not been deleted they
	 * will be removed.
	 */
	private void onCloseCleanup() {
		try {
			File fileToBeDeleted;
			for (String tempFile: tempFiles) {
				fileToBeDeleted = new File(tempFile);
				if (fileToBeDeleted.exists()) {
					fileToBeDeleted.delete();
				}
			}
			stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes core directories and launches the application.
	 * @param args
	 */
	public static void main(String args[]) {
		String command = "rm -rf ./.Game 2> /dev/null";
		try {
			ShellHelper.execute(command);
			command = "mkdir -p ./Creations";
			ShellHelper.execute(command);
			command = "mkdir -p ./Creations/.temp ./Creations/.tempPhotos ./.Game";
			ShellHelper.execute(command);
		} catch (Exception e) {
			return;
		}
		
		launch(args);
	}
	
	/**
	 * Deletes all existing user made creations.
	 */
	public static void deleteAllCreations() {
		try {
			ShellHelper.execute("rm -r ./Creations");
		} catch (Exception e) {
			return;
		}
	}
}
