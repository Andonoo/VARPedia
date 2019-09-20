package wikiSpeak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/***
 * Singleton class to represent the UI for viewing creations
 * @author Xiaobin Lin
 *
 */
public class ViewCreationsUI{
	private static TableView _tableView;
	private static Runnable _onBack;
	private static Scene _scene = null;

	private ViewCreationsUI(Runnable onBack) {
		_tableView = new TableView();
		_onBack = onBack;

		// Create and bind action to onBack
		Button backButton = new Button("Back");
		backButton.setOnAction(event-> _onBack.run());

		// Create columns for the UI
		List<String> creationFieldNames = Arrays.asList("creationName", "duration", "play", "delete");
		List<String> tableColumnNames = Arrays.asList("Name", "Duration", "Play", "Delete");
		double[] widthMultiplier = {0.4, 0.5/3, 0.5/3, 0.5/3};
		List<TableColumn<String, Creation>> columns = new ArrayList<TableColumn<String, Creation>>();
		for (int i = 0; i < creationFieldNames.size(); i++) {
			TableColumn<String, Creation> col = new TableColumn<>(tableColumnNames.get(i));
			col.setCellValueFactory(new PropertyValueFactory<>(creationFieldNames.get(i)));
			col.prefWidthProperty().bind(_tableView.widthProperty().multiply(widthMultiplier[i]));
			columns.add(col);
		}
		_tableView.getColumns().addAll(columns);
		
		// Set UI
		GridPane layout = new GridPane();
		layout.setPadding(new Insets(10,10,10,10));
		layout.getColumnConstraints().add(new ColumnConstraints(980));
		layout.setGridLinesVisible(true);
		layout.add(buttonWrapper(backButton), 0, 0,1, 1);
		layout.add(_tableView, 0, 1,1, 1);

		_scene = new Scene(layout, 1000, 600);
	}

	/***
	 * Static method to get instance of Singleton class
	 * @param onBack
	 * @return
	 */
	public static Scene getInstance(Runnable onBack)
	{
		if (_scene == null){
			new ViewCreationsUI(onBack);
		}
		// Ensure that the list is up to date
		refreshTableAsync();
		return _scene;
	}

	/***
	 * A wrapper so that the button can take the full width
	 * @param button
	 * @return
	 */
	private HBox buttonWrapper(Button button){
		HBox hbox = new HBox();
		hbox.getChildren().add(button);
		HBox.setHgrow(button, Priority.ALWAYS);
		button.setMaxWidth(Double.MAX_VALUE);
		return hbox;
	}
	
	/***
	 * Refresh the table to reflect any change in the ./Creations folder
	 */
	private static void refreshTableAsync() {
		_tableView.getItems().clear();
		_tableView.setPlaceholder(new Label("Loading..."));
		Thread worker = new Thread(()->{
			List<Creation> creations = getCreations();
			Platform.runLater(()->{
				// Hint that there are no creations
				_tableView.setPlaceholder(new Label("There are no creations"));

				// If there are creations, placeholder will not be shown
				for (int i = 0; i < creations.size(); i++) {
					_tableView.getItems().add(creations.get(i));
				}
			});
		});
		worker.start();
	}
	
	/***
	 * Get creations using bash by getting contents from ./Creations
	 * @return
	 * @throws RuntimeException
	 */
	private static List<Creation> getCreations() throws RuntimeException{
		List<String> creationNames = new ArrayList<String>();
		List<Creation> creations = new ArrayList<Creation>();
		try {
            String command = String.format("ls -a ./Creations 2> /dev/null | grep -Po \"((.+)(?=\\.mp4))\"");
			creationNames = ShellHelper.execute(command);
		} catch (Exception e) {
		    // Return empty list of creations to indicate there are no creations
			return new ArrayList<Creation>();
		}
		
		// Create a list of Creation objects
		for (int i = 0; i < creationNames.size(); i++) {
			Creation creation = new Creation(creationNames.get(i), ()->{refreshTableAsync();});
			creations.add(creation);
		}
		
		return creations;
	}

}
