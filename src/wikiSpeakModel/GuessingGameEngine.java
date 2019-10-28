package wikiSpeakModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

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
	private GuessMedia _currentMedia;
	
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
		loadScoreBoard();
		addPlayerToBoard();
	}
	
	/**
	 * Return the type of the game, video, audio, text etc.
	 * @return
	 */
	public MediaType getCategory() {
		return _gameType;
	}
	
	/**
	 * Produces the media elements (videos, audio clips, text) required for the game to begin. NOTE: This method should
	 * be called on a background thread.
	 * @throws Exception
	 */
	public void prepareGame() throws Exception {
		if (_category.equals(GameCategory.Creations)) {
			getCreationMedia();
		} else {
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
	}
	
	/**
	 * Method called at end of game in order to save the ScoreBoard through serialization.
	 */
	public void saveScoreBoard() {
		File boardFile = new File(".ScoreBoard");
		try {
			if (!boardFile.exists()) {
				boardFile.createNewFile();
			}
			
			FileOutputStream fileOutput = new FileOutputStream(boardFile);
			ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
			
			objectOutput.writeObject(_scoreBoard);
			
			fileOutput.close();
			objectOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method called to determine if the player's guess is correct.
	 * @param guess
	 * @return
	 */
	public boolean checkGuess(String guess) {
		if (_currentMedia.checkGuess(guess)) {
			_currentPlayerScore.addGuess(guess, _currentMedia.getAnswer(), true);
			return true;
		} else {
			_currentPlayerScore.addGuess(guess, _currentMedia.getAnswer(), false);
			return false;
		}
	}
	
	public String getAnswer() {
		return(_currentMedia.getAnswer());
	}
	
	/**
	 * Returns the next GuessMedia component for the user to guess.
	 * 
	 * @return
	 */
	public GuessMedia nextMedia() {
		_currentMedia = _guessMediaElements.get(_currentMediaElementIndex);
		_currentMediaElementIndex ++;
		_currentPlayerScore.incrementRounds();
		return _currentMedia;
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
	 * Method called if the user has selected to use their own creations for viewing. Populates
	 * _guessMediaElements with creation media elements.
	 * @throws Exception 
	 */
	private void getCreationMedia() throws Exception {
		File creationDir = new File("Creations/");
		File[] creations = creationDir.listFiles();
		if (creations.length == 0) {
			throw new Exception("There are no creations");
		}
		for (int i = 1; i <= _numRounds; i ++) {
			switch(_gameType) {
			case Video:
				getRandomCreationVideo(creations);
			break;
			case Audio:
				getRandomCreationAudio(creations);
			break;
			case Text:
				getRandomCreationText(creations);
			break;
			}
		}
	}
	
	/**
	 * Takes the list of directory of a set of creations and adds a GuessMedia element for 
	 * a random creation's video. 
	 * @param creations
	 */
	private void getRandomCreationVideo(File[] creations) {
		Random rand = new Random();
		File randomCreation = creations[rand.nextInt(creations.length)];
		File creationVideo = new File(randomCreation + "/" + randomCreation.getName() + ".mp4");
		_guessMediaElements.add(new GuessMedia(MediaType.Video, creationVideo, getCreationSearchTerm(randomCreation)));
	}
	
	/**
	 * Takes the list of directory of a set of creations and adds a GuessMedia element for 
	 * a random creation's audio. 
	 * @param creations
	 */
	private void getRandomCreationAudio(File[] creations) {
		Random rand = new Random();
		File randomCreation = creations[rand.nextInt(creations.length)];
		File creationAudio = new File(randomCreation + "/" + randomCreation.getName() + ".wav");
		_guessMediaElements.add(new GuessMedia(MediaType.Video, creationAudio, getCreationSearchTerm(randomCreation)));
	}
	
	/**
	 * Takes the list of directory of a set of creations and adds a GuessMedia element for 
	 * a random creation's text. 
	 * @param creations
	 */
	private void getRandomCreationText(File[] creations) {
		Random rand = new Random();
		File randomCreation = creations[rand.nextInt(creations.length)];
		File creationText = new File(randomCreation + "/" + randomCreation.getName() + ".txt");
		String contents = "";
		try {
			contents = new String(Files.readAllBytes(Paths.get(creationText.getCanonicalPath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		_guessMediaElements.add(new GuessMedia(MediaType.Video, contents, getCreationSearchTerm(randomCreation)));
	}
	
	private String getCreationSearchTerm(File creation) {
		File termDir = new File("Creations/" + creation.getName() + "/" + "Term");
		File[] termDirContents = termDir.listFiles();
		return termDirContents[0].getName();
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
			File video = new File(dirForRound + String.format("%s.mp4", term));
			GuessMedia guessVideo = new GuessMedia(_gameType, video, term);
			_guessMediaElements.add(guessVideo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Builds an text chunk for the user's current input.
	 * @throws Exception 
	 */
	private void createText() throws Exception {
		String term = getRandomTerm();
		String text = MediaHelper.searchWiki(term);
		GuessMedia textMedia = new GuessMedia(_gameType, text, term);
		_guessMediaElements.add(textMedia);
	}

	/**
	 * Builds an audio chunk for the user's current input.
	 * @throws Exception 
	 */
	private void createAudio(int audioNumber) throws Exception {
		String term = getRandomTerm();
		String text = MediaHelper.searchWiki(term);
		MediaHelper mediaHelper = new MediaHelper(String.format(".Game/.Round%s/", audioNumber));
		try {
			mediaHelper.createAudioChunk(text, "kal_diphone", term);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File audio = new File(String.format(".Game/.Round%s/%s.wav", audioNumber, term));
		GuessMedia guessMedia = new GuessMedia(_gameType, audio, term);
		_guessMediaElements.add(guessMedia);
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
		String difficulty;
		if (_age < 7) {
			difficulty = "Easy";
		} else if (_age < 10) {
			difficulty = "Medium";
		} else {
			difficulty = "Hard";
		}
		String pathToTxt = String.format("res/Quiz/%s/%s.txt", _category.toString(), difficulty);
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
			txtScanner.close();
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
			_currentPlayerScore = new GameRecord(_playerName);
			_scoreBoard.put(_playerName, _currentPlayerScore);
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
				
				_scoreBoard = (Map<String, GameRecord>) ois.readObject();
				
				fis.close();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			_scoreBoard = new HashMap<String, GameRecord>();
		}
	}
	
	/**
	 * @return The score of the current player.
	 */
	public GameRecord getPlayerScore() {
		return _currentPlayerScore;
	}
}
