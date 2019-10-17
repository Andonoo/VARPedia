package wikiSpeakModel;

import javafx.event.ActionEvent;

public class GuessCreation {
	private String _guessTerm;

	public boolean checkGuess(String guessTerm) {
		return _guessTerm.equals(guessTerm);
	}
}
