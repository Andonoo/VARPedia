package wikiSpeakController;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.GameRecord;
import wikiSpeakModel.PlayerGuess;

/**
 * Controller class for the scoreboard displayed to the user at the end of the game.
 * 
 * @author Andrew Donovan
 *
 */
public class EndGameScoreBoardController{
	private GameRecord _playerScore;
	@FXML 
	private Text _scoreText;
	@FXML 
	private Text _netScoreText;
	@FXML
	private TableView<PlayerGuess> _scoreBoard;
	@FXML
	private TableColumn<PlayerGuess, String> _roundColumn;
	@FXML
	private TableColumn<PlayerGuess, String> _guessColumn;
	@FXML
	private TableColumn<PlayerGuess, String> _answerColumn;
	@FXML
	private TableColumn<PlayerGuess, String> _pointsColumn;
	
	/**
	 * Method called on launching of the end of game scoreboard. Initializes table value factories.
	 */
	@FXML
	private void initialize() {		
		if (_scoreBoard != null) {
			_roundColumn.setCellValueFactory(cellData -> cellData.getValue().getRound());
			_guessColumn.setCellValueFactory(cellData -> cellData.getValue().getGuess());
			_answerColumn.setCellValueFactory(cellData -> cellData.getValue().getAnswer());
			_pointsColumn.setCellValueFactory(cellData -> cellData.getValue().getNetPoints());
		}
	}
	
	/**
	 * Sets the player's score which we're concerned with.
	 * @param playerScore
	 */
	public void setPlayerScore(GameRecord playerScore) {
		_playerScore = playerScore;
		
		List<PlayerGuess> playerGuesses = _playerScore.getGuesses();
		ObservableList<PlayerGuess> content = FXCollections.observableArrayList(playerGuesses);
		_scoreBoard.setItems(content);
		_scoreText.setText("Your total score: " + _playerScore.getTotalScore());
		
		if (_playerScore._getScoreThisRound() >= 0) {
			_netScoreText.setFill(Color.GREEN);
			_netScoreText.setText(" (+" + _playerScore._getScoreThisRound() + ")");
		} else {
			_netScoreText.setFill(Color.RED);
			_netScoreText.setText(" (" + _playerScore._getScoreThisRound() + ")");
		}
		
		_playerScore.endOfGame();
	}
	
	/**
	 * Action listener for home button.
	 * @param event
	 */
	@FXML
	private void onHome(ActionEvent event) {
		try {
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
			stage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Action listener for play again button.
	 * @param event
	 */
	@FXML
	private void onPlayAgain(ActionEvent event) {
		try {
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.GameSetup));
			stage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
