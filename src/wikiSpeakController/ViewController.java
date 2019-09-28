package wikiSpeakController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import wikiSpeak.Creation;
import wikiSpeak.Main;
import wikiSpeak.ShellHelper;

public class ViewController {
	@FXML
	private TableView creationTable;
	
	@FXML
    public void initialize() {
        loadData();
		refreshTableAsync();
    }
	
	private void loadData() {
		// Create columns for the UI
		List<String> creationFieldNames = Arrays.asList("creationName", "duration", "play", "delete");
		List<String> tableColumnNames = Arrays.asList("Name", "Duration", "Play", "Delete");
		double[] widthMultiplier = {0.4, 0.5/3, 0.5/3, 0.5/3};
		List<TableColumn<String, Creation>> columns = new ArrayList<TableColumn<String, Creation>>();
		for (int i = 0; i < creationFieldNames.size(); i++) {
			TableColumn<String, Creation> col = new TableColumn<>(tableColumnNames.get(i));
			col.setCellValueFactory(new PropertyValueFactory<>(creationFieldNames.get(i)));
			col.prefWidthProperty().bind(creationTable.widthProperty().multiply(widthMultiplier[i]));
			columns.add(col);
		}
		creationTable.getColumns().addAll(columns);
	} 
//	
	@FXML
	private void onBackBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(Main.getLayout());
		stage.setScene(scene);
	}
	
	/***
	 * Refresh the table to reflect any change in the ./Creations folder
	 */
	private void refreshTableAsync() {
		creationTable.getItems().clear();
		creationTable.setPlaceholder(new Label("Loading..."));
		Thread worker = new Thread(()->{
			List<Creation> creations = getCreations();
			Platform.runLater(()->{
				// Hint that there are no creations
				creationTable.setPlaceholder(new Label("There are no creations"));

				// If there are creations, placeholder will not be shown
				for (int i = 0; i < creations.size(); i++) {
					creationTable.getItems().add(creations.get(i));
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
	private List<Creation> getCreations() throws RuntimeException{
		List<String> creationNames = new ArrayList<String>();
		List<Creation> creations = new ArrayList<Creation>();
		try {
			// Assuming if a folder exists, a creation exists
            String command = String.format("ls ./Creations 2> /dev/null");
			creationNames = ShellHelper.execute(command);
			for (int i = 0; i < creationNames.size(); i++) {
				creationNames.set(i, "./Creations/" + creationNames.get(i) + "/" + creationNames.get(i) + "Creation" + ".mp4");
			}
		} catch (Exception e) {
		    // Return empty list of creations to indicate there are no creations
			return new ArrayList<Creation>();
		}
		
		// Create a list of Creation objects
		for (int i = 0; i < creationNames.size(); i++) {
			Creation creation = new Creation(creationNames.get(i), () -> {refreshTableAsync();});
			creations.add(creation);
		}
		
		return creations;
	}
	
	
}
