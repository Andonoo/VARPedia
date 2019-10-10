package wikiSpeak;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher;
import wikiSpeakController.ShellHelper;
import wikiSpeakController.SceneSwitcher.SceneOption;

/**
 * Main application method for VARpedia
 * @author Andrew Donovan, Xiaobin Lin
 * 
 */
public class Main extends Application{	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String args[]) {
		String command = "mkdir -p ./Creations";
		try {
//			deleteAllCreations();
			ShellHelper.execute(command);
			command = "mkdir -p ./Creations/.temp";
			ShellHelper.execute(command);
			command = "mkdir -p ./Creations/.tempPhotos";
			ShellHelper.execute(command);
		} catch (Exception e) {
			return;
		}
		
		launch(args);
	}
	
	public static void deleteAllCreations() {
		try {
			ShellHelper.execute("rm -r ./Creations");
		} catch (Exception e) {
			return;
		}
	}
}
