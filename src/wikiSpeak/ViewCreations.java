package wikiSpeak;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewCreations {
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ViewCreations.class.getResource("ViewCreations.fxml"));
		Parent layout = loader.load();
		return layout;
	}
}
