package wikiSpeak;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application method for VARpedia
 * @author Andrew Donovan, Xiaobin Lin
 * 
 */
public class Main extends Application{	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(getLayout());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Get the layout of the Main UI
	 * @return
	 * @throws IOException
	 */
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("Main.fxml"));
		Parent layout = loader.load();
		return layout;
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
