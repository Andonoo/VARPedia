package wikiSpeakModel;

import java.io.Serializable;

/**
 * Data structure used to store the record for any player.
 * 
 * @author Andrew Donovan
 *
 */
public class GameRecord implements Serializable {
	private int _correctGuesses;
	private int _wrongGuesses;
	private int _totalRoundsPlayed;
	
	public GameRecord() {
		_correctGuesses = 0;
		_wrongGuesses = 0;
		_totalRoundsPlayed = 0;
	}
	
	public void incrementCorrect() {
		_correctGuesses++;
	}
	
	public void incrementWrong() {
		_wrongGuesses++;
	}

	public void incrementRounds() {
		_totalRoundsPlayed++;
	}
}
