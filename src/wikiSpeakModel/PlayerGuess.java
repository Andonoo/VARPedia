package wikiSpeakModel;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerGuess implements Serializable {
	private String _guess;
	private String _answer;
	private String _netPoints;
	private String _round;
	
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
	
	public StringProperty getRound() {
		return new SimpleStringProperty(_round);
	}
	
	public StringProperty getGuess() {
		return new SimpleStringProperty(_guess);
	}
	
	public StringProperty getAnswer() {
		return new SimpleStringProperty(_answer);
	}
	
	public StringProperty getNetPoints() {
		return new SimpleStringProperty(_netPoints);
	}
}
