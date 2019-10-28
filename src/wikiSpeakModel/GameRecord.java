package wikiSpeakModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data structure used to store the record for any player.
 * 
 * @author Andrew Donovan
 *
 */
public class GameRecord implements Serializable {
	private int _round;
	private String _player;
	private List<PlayerGuess> _guessesThisGame;
	private int _totalScore;
	private int _netScoreThisRound;
	private int _correctGuesses;
	private int _incorrectGuesses;
	
	/**
	 * Creates a game record for the provided player. Sets initial values to 0.
	 * 
	 * @param playerName
	 */
	public GameRecord(String playerName) {
		_round = 0;
		_correctGuesses = 0;
		_incorrectGuesses = 0;
		_netScoreThisRound = 0;
		_totalScore = 0;
		_player = playerName;
		_guessesThisGame = new ArrayList<PlayerGuess>();
	}
	
	/**
	 * Adds a guess to the record.
	 * @param guess the players's answer
	 * @param answer the correct answer
	 * @param correct whether their guess was correct
	 */
	public void addGuess(String guess, String answer, boolean correct) {
		_guessesThisGame.add(new PlayerGuess(guess, answer, _round, correct));
		if (correct) {
			_correctGuesses ++;
			_totalScore++ ;
			_netScoreThisRound++;
		} else {
			_incorrectGuesses ++;
			_totalScore--;
			_netScoreThisRound--;
		}
	}
	
	/**
	 * Increment the round for recording purposes.
	 */
	public void incrementRounds() {
		_round++;
	}
	
	/**
	 * Returns a list of guesses for this current game.
	 * @return
	 */
	public List<PlayerGuess> getGuesses() {
		return _guessesThisGame;
	}
	
	/**
	 * Returns the players score from all time.
	 * @return
	 */
	public int getTotalScore() {
		return _totalScore;
	}
	
	/**
	 * Returns the players net score from this game.
	 * @return
	 */
	public int _getScoreThisRound() {
		return _netScoreThisRound;
	}
	
	/**
	 * Perform end of game cleanup (deleting guesses so that they will not be shown during the next 
	 * game for this player).
	 */
	public void endOfGame() {
		_guessesThisGame = new ArrayList<PlayerGuess>();
		_round = 0;
		_netScoreThisRound = 0;
	}
	
	/**
	 * Returns the players score from all time as an observable.
	 * @return
	 */
	public StringProperty getTotalScoreForTable() {
		return new SimpleStringProperty(Integer.toString(_totalScore));
	}
	
	/**
	 * Returns the players as an observable.
	 * @return
	 */
	public StringProperty getPlayerForTable() {
		return new SimpleStringProperty(_player);
	}
	
	/**
	 * Returns the players correct guesses as an observable.
	 * @return
	 */
	public StringProperty getCorrectGuessesForTable() {
		return new SimpleStringProperty(Integer.toString(_correctGuesses));
	}
	
	/**
	 * Returns the players incorrect guesses as an observable.
	 * @return
	 */
	public StringProperty getIncorrectGuessesForTable() {
		return new SimpleStringProperty(Integer.toString(_incorrectGuesses));
	}
	
	/**
	 * Returns the players incorrect guesses as an observable.
	 * @return
	 */
	public StringProperty getAccuracyForTable() {
		float accuracy = _correctGuesses * 100f / (_correctGuesses + _incorrectGuesses);
		int accuracyRounded = Math.round(accuracy);
		return new SimpleStringProperty(Integer.toString(accuracyRounded) + "%");
	}
}
