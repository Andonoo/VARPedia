package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
			e.printStackTrace();
		}
    }
	
	/**
	 * Go to ViewCreation UI
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onViewBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.ViewCreations));
		stage.setScene(scene);
	}
	
	/**
	 * Go to new creation wizard
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onCreateBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.Search));
		stage.setScene(scene);
	}
}
