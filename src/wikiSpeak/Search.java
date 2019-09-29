package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * A class to retrieve the layout for Searching
 * @author student
 *
 */
public class Search {
	/**
	 * Get the layout for searching
	 * @return
	 * @throws IOException
	 */
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Search.class.getResource("Search.fxml"));
		Parent layout = loader.load();
		return layout;
	}
}
