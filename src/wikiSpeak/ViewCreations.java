package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * A class to retrieve the layout for viewing creation
 * @author student
 *
 */
public class ViewCreations {
	/**
	 * Get the layout for viewing creation
	 * @return
	 * @throws IOException
	 */
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ViewCreations.class.getResource("ViewCreations.fxml"));
		Parent layout = loader.load();
		return layout;
	}
}
