package wikiSpeakModel;

import java.io.File;

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
	private String _guessText;
	private MediaType _mediaType;

	/**
	 * Creates a video or audio GuessMedia instance.
	 * @param type of media (audio or video)
	 * @param media 
	 */
	public GuessMedia(MediaType type, File media, String term) {
		_mediaType = type;
		_guessTerm = term;
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
	public GuessMedia(MediaType type, String text, String term) {
		_mediaType = type;
		_guessText = text;
		_guessTerm = term;
	}
	
	/**
	 * Checks if the user's guess is correct.
	 * @param guessTerm
	 * @return
	 */
	public boolean checkGuess(String guessTerm) {
		boolean isCorrect = false;
		if (_guessTerm.toLowerCase().equals(guessTerm.toLowerCase())){
			return true;
		} else {
			// Very rudimentary way of trying to remove plurals
			isCorrect = checkTermIgnorePlural(_guessTerm.toLowerCase(), guessTerm.toLowerCase());
			if (isCorrect) {
				return true;
			}
			isCorrect = checkTermIgnorePlural(guessTerm.toLowerCase(), _guessTerm.toLowerCase());
		}
		return isCorrect;
	}
	
	/**
	 * Get the answer to the GuessMedia.
	 * @return
	 */
	public String getAnswer() {
		return _guessTerm;
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
			default:
			break;	
		}
		return null;
	}
	
	/**
	 * Returns this media objects text.
	 * @return
	 */
	public String getText() {
		return _guessText;
	}
	
	/**
	 * A very simple method to check is the word has the same meaning ignoring its plurality
	 * @param singular
	 * @param potentiallyPlural
	 * @return
	 */
	private boolean checkTermIgnorePlural(String singular, String potentiallyPlural) {
		int length = potentiallyPlural.length();
		if (length > 0) {
			if (potentiallyPlural.substring(0, length-1).equals(singular) && potentiallyPlural.charAt(length-1) == 's') {
				return true;
			} 
			if (potentiallyPlural.charAt(length-1) == 'y') {
				// Strip out the y
				potentiallyPlural = potentiallyPlural.substring(0, length-1);
				// Replace it with ies
				potentiallyPlural += "ies";
				if (potentiallyPlural.equals(singular)) {
					return true;
				}
			}
		}
		return false;
	}
}
