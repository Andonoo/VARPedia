package wikiSpeakController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import wikiSpeakModel.GameRecord;

/**
 * Class used to control the displaying of the leaderboard data from the leaderboard view.
 * 
 * @author Andrew Donovan
 */
public class LeaderboardController {
	private Map<String, GameRecord> _leaderboard;
	private List<GameRecord> _playerScores;
	
	@FXML 
	private TableView<GameRecord> _leaderboardTable;	
	@FXML 
	private TableColumn<GameRecord, String> _playerColumn;
	@FXML 
	private TableColumn<GameRecord, String> _scoreColumn;
	@FXML 
	private TableColumn<GameRecord, String> _correctColumn;
	@FXML 
	private TableColumn<GameRecord, String> _incorrectColumn;
	@FXML 
	private TableColumn<GameRecord, String> _accuracyColumn;
	
	/**
	 * Method called on loading of leaderboard scene. Initializes the table component.
	 */
	@FXML
	private void initialize() {
		loadScoreBoard();
		loadPlayerScores();
		
		ObservableList<GameRecord> tableContent = FXCollections.observableArrayList(_playerScores);
		
		if (_leaderboard != null) {
			_playerColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayerForTable());
			_scoreColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalScoreForTable());
			_correctColumn.setCellValueFactory(cellData -> cellData.getValue().getCorrectGuessesForTable());
			_incorrectColumn.setCellValueFactory(cellData -> cellData.getValue().getIncorrectGuessesForTable());
			_accuracyColumn.setCellValueFactory(cellData -> cellData.getValue().getAccuracyForTable());
			_leaderboardTable.setItems(tableContent);
		}
	}
	
	/**
	 * Handler class for the back button. Returns the user to the main menu.
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void onBack(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneSwitcher.SceneOption.Main));
		stage.setScene(scene);
	}
	
	/**
	 * Iterates through leaderboard hashmap to retrieve player scores.
	 */
	private void loadPlayerScores() {
		_playerScores = new ArrayList<GameRecord>();
		for (String player: _leaderboard.keySet()) {
			_playerScores.add(_leaderboard.get(player));
		}
	}

	/**
	 * Loads the existing scoreboard by deserialize the ScoreBoard file or creates a new one.
	 */
	private void loadScoreBoard() {
		File boardFile = new File(".ScoreBoard");
		if (boardFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(boardFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				_leaderboard = (Map<String, GameRecord>) ois.readObject();
				
				fis.close();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			_leaderboard = new HashMap<String, GameRecord>();
		}
	}
}
