package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeak.Search;
import wikiSpeak.ShellHelper;
import wikiSpeak.ViewCreations;

/**
 * Controller class for main menu UI component.
 *
 */
public class MainController {
	
	@FXML
    public void initialize() {
		// Remove all folders that doesn't have a creation
        String command = "xargs rm -rf <<< $(find ./Creations -mindepth 1 -maxdepth 1 -type d '!' -exec sh -c 'ls -1 \"{}\"|egrep -i -q \"*.(mp4)$\"' ';' -print)";
        try {
			ShellHelper.execute(command);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@FXML
	private void onViewBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(ViewCreations.getLayout());
		stage.setScene(scene);
	}
	
	@FXML
	private void onCreateBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(Search.getLayout());
		stage.setScene(scene);
	}
	
//	@FXML
//	private void onViewBtnClicked(ActionEvent event) throws IOException {
//		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
//		Scene scene = new Scene(Main.getLayout());
//		stage.setScene(ViewCreationsUI.getInstance(()->stage.setScene(scene)));
//	}
}
