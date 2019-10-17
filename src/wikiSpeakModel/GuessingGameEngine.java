package wikiSpeakModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
	private int _numGames;
	private List<GuessCreation> _creations;
	private GuessCreation _currentCreation;
	
	/**
	 * Creates an instance of the game for the user's given inputs.
	 * @param playerName
	 * @param age
	 * @param numGames
	 * @param category
	 */
	public GuessingGameEngine(String playerName, int age, int numGames, GameCategory category) {
		_playerName = playerName;
		_age = age;
		_numGames = numGames;
		_category = category;
	}
	
	/**
	 * Determines whether the player's guess matches the current creation in play.
	 * @param guessTerm
	 * @return
	 */
	public boolean checkGuess(String guessTerm) {
		return _currentCreation.checkGuess(guessTerm);
	}
	
	/**
	 * Builds a creation for the user's current input.
	 */
	public void buildCreation() {
		String term = getRandomTerm();
		String pathForImages = String.format(".Game/.GameCreations/.%s", term);
		FlickrHelper.getImages(, term);
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
		String pathToTxt = String.format("/.Game/.GameTerms/Age%s/%s.txt", Integer.toString(_age), ShellHelper.WrapString(_category.toString()));
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
}
