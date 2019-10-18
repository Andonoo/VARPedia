package wikiSpeakModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import wikiSpeakController.ShellHelper;

/**
 * Class which will be used to take the user's inputs to the guessing game (age, category, difficulty ...) 
 * and use them to create an appropriate creation(s) to guess from. Also manages the guessing game logic 
 * such as determining whether a guess is correct. 
 * 
 * @author Andrew Donovan
 *
 */
public class GuessingGameEngine {
	private String _playerName;
	private int _age;
	private GameCategory _category;
	private int _numRounds;
	private List<GuessMedia> _guessMediaElements;
	private int _currentMediaElementIndex;
	private MediaType _gameType;
	private Map<String, GameRecord> _scoreBoard;
	private GameRecord _currentPlayerScore;
	
	/**
	 * Creates an instance of the game for the user's given inputs.
	 * @param playerName
	 * @param age
	 * @param numGames
	 * @param category
	 */
	public GuessingGameEngine(String playerName, int age, int numRounds, GameCategory category, MediaType gameType) {
		_playerName = playerName;
		_age = age;
		_numRounds = numRounds;
		_category = category;
		_gameType = gameType;
		_guessMediaElements = new ArrayList<GuessMedia>();
		_currentMediaElementIndex = 0;
		_scoreBoard = new HashMap<String, GameRecord>();
		addPlayerToBoard();
	}

	/**
	 * Produces the media elements (videos, audio clips, text) required for the game to begin. NOTE: This method should
	 * be called on a background thread.
	 */
	public void prepareGame() {
		switch(_gameType) {
			case Video:
				for (int i = 1; i <= _numRounds; i ++) {
					createVideo(i);
				}
			break;
			case Audio:
				for (int i = 1; i <= _numRounds; i ++) {
					createAudio(i);
				}
			break;
			case Text:
				for (int i = 1; i <= _numRounds; i ++) {
					createText();
				}
			break;
		}
	}
	
	/**
	 * Returns the next GuessMedia component for the user to guess.
	 * 
	 * @return
	 */
	public GuessMedia nextMedia() {
		GuessMedia media = _guessMediaElements.get(_currentMediaElementIndex);
		_currentMediaElementIndex ++;
		return media;
	}
	
	/**
	 * Determines if there is another GuessMedia component availible for the user.
	 * @return
	 */
	public boolean hasNextMedia() {
		if (_currentMediaElementIndex >= _guessMediaElements.size()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Builds a video for the user's current input.
	 */
	private void createVideo(int videoNumber) {
		String term = getRandomTerm();
		String dirForRound = String.format(".Game/.Round%s/", videoNumber);
		FlickrHelper.getImages(dirForRound, term);
		MediaHelper mediaHelper = new MediaHelper(dirForRound);
		try {
			mediaHelper.createSlideShowToLength(10, 5, term, ".tempPhotos/", "");
			File video = new File(dirForRound + "term.mp4");
			GuessMedia guessVideo = new GuessMedia(_gameType, video);
			_guessMediaElements.add(guessVideo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Builds an text chunk for the user's current input.
	 */
	private void createText() {
		String term = getRandomTerm();
		List<String> text = fetchText(term);
		GuessMedia textMedia = new GuessMedia(_gameType, text);
		_guessMediaElements.add(textMedia);
	}
	
	/**
	 * Returns a list of strings from wikit for a random term with the current inputs.
	 * @return wikit result
	 */
	private List<String> fetchText(String term) {
		boolean successful = false;
		List<String> output = null;
		while (!successful) {
			String command = String.format("wikit %s", term);
			try {
				output = ShellHelper.execute(command);
				if (output.size() != 0 && !output.get(0).equals("not found :^(")) {
					successful = true;
				} else {
					System.out.println("Term: " + term + " should be removed as it is not wikit-compatible");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
 
	/**
	 * Builds an audio chunk for the user's current input.
	 */
	private void createAudio(int audioNumber) {
		String term = getRandomTerm();
		List<String> textList = fetchText(term);
		String text = "";
		for (String s: textList) {
			text = text + s;
		}
		MediaHelper mediaHelper = new MediaHelper(String.format(".Game/.Round%s/", audioNumber));
		try {
			mediaHelper.createAudioChunk(text, "kal_diphone", term);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File audio = new File(String.format(".Game/.Round%s/%s.wav", audioNumber, term));
		GuessMedia guessMedia = new GuessMedia(_gameType, audio);
	}
	
	/**
	 * Retrieves a random term for the current user inputs.
	 * @return
	 */
	private String getRandomTerm() {
		List<String> terms = getTerms();
		Random randomGenerator = new Random();
		int randElement = randomGenerator.nextInt(terms.size());
		return terms.get(randElement);
	}
	
	/**
	 * Retrieves a list of potential terms for the user's current inputs.
	 * @return
	 */
	private List<String> getTerms() {
		String pathToTxt = String.format(".GameTerms/Age%s/%s.txt", Integer.toString(_age), ShellHelper.WrapString(_category.toString()));
		File terms = new File(pathToTxt);
		List<String> strings = txtToList(terms);
		return strings;
	}

	/**
	 * Takes the provided file and returns a list of strings representing the lines of it's 
	 * content.
	 * @param txtFile to be converted
	 * @return list of strings representing file
	 */
	private List<String> txtToList(File txtFile) {
		List<String> strings = new ArrayList<String>();
		try {
			Scanner txtScanner = new Scanner(txtFile);
			while (txtScanner.hasNextLine()) {
				String line = txtScanner.nextLine();
				if (!line.isBlank()) {
					strings.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return strings;
	}
	
	/**
	 * Method used to add the current player to the scoreboard or retrieve the current players score ready to be updated.
	 */
	private void addPlayerToBoard() {
		if (_scoreBoard.containsKey(_playerName)) {
			_currentPlayerScore = _scoreBoard.get(_playerName);
		} else {
			_currentPlayerScore = new GameRecord();
			_scoreBoard.put(_playerName, _currentPlayerScore);
		}
	}
}
