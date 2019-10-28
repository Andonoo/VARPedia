package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller class for main menu UI component.
 */
public class MainController {
	@FXML
    public void initialize() {
		// Remove all folders that doesn't have a creation
        String command = "xargs -I{} rm -rf {}<<< $(find ./Creations -mindepth 1 -maxdepth 1 -type d '!' -exec sh -c 'ls -1 \"{}\"|egrep -i -q \"*.(mp4)$\"' ';' -print)";
        try {
			ShellHelper.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Listener method for the view button. Loads the ViewCreation menu.
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onViewBtnClicked(Event event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.ViewCreations));
		stage.setScene(scene);
	}
	
	/**
	 * Listener method for the create button. Loads the creation wizard.
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onCreateBtnClicked(Event event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.Search));
		stage.setScene(scene);
	}
	
	/**
	 * Listener method for the play button. Loads the game setup.
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onPlayBtnClicked(Event event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.GameSetup));
		stage.setScene(scene);
	}
	
	/**
	 * Listener method for the leaderboard button. Loads the game leaderboard.
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onLeaderboardBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.Leaderboard));
		stage.setScene(scene);
	}
}
