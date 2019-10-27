package wikiSpeakController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.Creation;
import wikiSpeakModel.GameRecord;
import wikiSpeakModel.Playable;

/**
 * Controller class for view creations UI component.
 * 
 */
public class ViewController {
	@FXML private TableView<Playable> _creationTable;
	@FXML private TableColumn<Playable, String> _creationNameCol;
	@FXML private TableColumn<Playable, Button> _playCol;
	@FXML private TableColumn<Playable, Button> _deleteCol;
	
	/**
	 * Sets initial state for UI component.
	 */
	@FXML
    public void initialize() {
		makeColumns();
		refreshTableAsync();
    }
	
	/**
	 * Create columns for the UI
	 */
	private void makeColumns() {
//		List<String> creationFieldNames = Arrays.asList("creationName", "play", "delete");
//		List<String> tableColumnNames = Arrays.asList("Name", "Play", "Delete");
//		double[] widthMultiplier = {0.4, 0.5/3, 0.5/3, 0.5/3};
//		List<TableColumn<String, Creation>> columns = new ArrayList<TableColumn<String, Creation>>();
//		for (int i = 0; i < creationFieldNames.size(); i++) {
//			TableColumn<String, Creation> col = new TableColumn<>(tableColumnNames.get(i));
//			col.setCellValueFactory(new PropertyValueFactory<>(creationFieldNames.get(i)));
//			col.prefWidthProperty().bind(creationTable.widthProperty().multiply(widthMultiplier[i]));
//			columns.add(col);
//		}
//		creationTable.getColumns().addAll(columns);
//		_playerColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayerForTable());
//		_playerColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayerForTable());
	}
	
	@FXML
	private void onHomeBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
		stage.setScene(scene);
	}
	
	/***
	 * Refresh the table to reflect any change in the ./Creations folder
	 */
	private void refreshTableAsync() {
		_creationTable.getItems().clear();
		_creationTable.setPlaceholder(new Label("Loading..."));
		Thread worker = new Thread(()->{
			List<Creation> creations = getCreations();
			ObservableList<Playable> oListCreation = FXCollections.observableArrayList(creations);
			Platform.runLater(()->{
				// Hint that there are no creations
				_creationTable.setPlaceholder(new Label("There are no creations"));
				_creationNameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPlayableName()));
				_playCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<Button>(cellData.getValue().getPlay()));
				_deleteCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<Button>(cellData.getValue().getDelete()));
				_creationTable.setItems(oListCreation);
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
