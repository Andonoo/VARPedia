package wikiSpeakModel;

import java.io.File;
import java.util.List;

import javafx.event.ActionEvent;

/**
 * Class to represent one media component for the user to guess against (text, audio or video).
 * 
 * @author Andrew Donovan
 *
 */
public class GuessMedia {
	private String _guessTerm;
	private File _guessVideo;
	private File _guessAudio;
	private List<String> _guessText;
	private MediaType _mediaType;

	/**
	 * Creates a video or audio GuessMedia instance.
	 * @param type of media (audio or video)
	 * @param media 
	 */
	public GuessMedia(MediaType type, File media) {
		_mediaType = type;
		switch(type) {
			case Video: 
				_guessVideo = media;
			break;
			case Audio: 
				_guessAudio = media;
			break;
		}
	}
	
	/**
	 * Creates a text GuessMedia instance.
	 * @param type should be MediaType.Text
	 * @param text
	 */
	public GuessMedia(MediaType type, List<String> text) {
		_mediaType = type;
		_guessText = text;
	}
	
	/**
	 * Checks if the user's guess is correct.
	 * @param guessTerm
	 * @return
	 */
	public boolean checkGuess(String guessTerm) {
		return _guessTerm.equals(guessTerm);
	}
	
	/**
	 * Returns this media objects video or audio file depending on the type.
	 * @return
	 */
	public File getAudioVideo() {
		switch(_mediaType) {
			case Audio: 
				return _guessAudio;
			case Video: 
				return _guessVideo;	
		}
		return null;
	}
	
	/**
	 * Returns this media objects text.
	 * @return
	 */
	public List<String> getText() {
		return _guessText;
	}
}
