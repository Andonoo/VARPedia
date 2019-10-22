package wikiSpeak;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		    	onClose();
		    }
		});
	}
	
	private void onClose() {
		try {
			String command = "rm -r .Game/.Round*";
			ShellHelper.execute(command);
			stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		String command = "rm -rf ./.Game 2> /dev/null";
		
		try {
//			deleteAllCreations();
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
	
	public static void deleteAllCreations() {
		try {
			ShellHelper.execute("rm -r ./Creations");
		} catch (Exception e) {
			return;
		}
	}
}
