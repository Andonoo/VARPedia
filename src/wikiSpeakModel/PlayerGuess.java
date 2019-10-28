package wikiSpeakModel;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class to represent a user's guess for a round. Encapsulates the guess for storage 
 * in their GameRecord. Also contains methods required to be displayed in a table.
 * 
 * @author Andrew Donovan
 */
public class PlayerGuess implements Serializable {
	private String _guess;
	private String _answer;
	private String _netPoints;
	private String _round;
	
	/**
	 * Initializes the players guess.
	 * @param guess the player's guess
	 * @param answer the correct answer
	 * @param round of the guess
	 * @param correct
	 */
	public PlayerGuess(String guess, String answer, int round, boolean correct) {
		_guess = guess;
		_answer = answer;
		_round = Integer.toString(round);
		if (correct) {
			_netPoints = "+1";
		} else {
			_netPoints = "-1";
		}
	}
	
	/**
	 * @return a string property of the round of this guess.
	 */
	public StringProperty getRound() {
		return new SimpleStringProperty(_round);
	}
	
	/**
	 * @return a string property for the guess.
	 */
	public StringProperty getGuess() {
		return new SimpleStringProperty(_guess);
	}
	
	/**
	 * @return a string property for the correct answer.
	 */
	public StringProperty getAnswer() {
		return new SimpleStringProperty(_answer);
	}
	
	/**
	 * @return a string property for the points lost or gained from this guess.
	 */
	public StringProperty getNetPoints() {
		return new SimpleStringProperty(_netPoints);
	}
}
