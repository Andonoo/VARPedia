package wikiSpeakController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.MediaHelper;

public class GameSetupController {
	@FXML Spinner<Integer> ageSpinner;
	@FXML Spinner<Integer> nOfGamesSpinner;
	@FXML TableView<String> categoryTV;
	@FXML TableColumn<String, String> categoryCol;
	@FXML RadioButton textRB;
	@FXML RadioButton audioRB;
	@FXML RadioButton videoRB;
	@FXML Button playBtn;
	
	ToggleGroup radioButtonGroup = new ToggleGroup(); 
	
	
	@FXML
	private void initialize() {
		loadCategoryTable();
		ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99));
		nOfGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
		
		// Toggle group ensures only one is selected at a time
		textRB.setToggleGroup(radioButtonGroup);
		audioRB.setToggleGroup(radioButtonGroup);
		videoRB.setToggleGroup(radioButtonGroup);
		audioRB.setSelected(true);
	}
	
	/***
	 * Load category names into table
	 */
	private void loadCategoryTable() {
		ObservableList<String> categories = FXCollections.observableArrayList("Fruits", "Animal", "Country", "Celebrity");
		categoryCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
		categoryTV.setItems(categories);
		categoryTV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	@FXML
	private void onHomeBtnClicked(ActionEvent event) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Return to main menu");
		alert.setContentText("Are you sure you want to go home? All of the progress will be lost");
		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes){
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(SceneSwitcher.getLayout(SceneOption.Main));
			stage.setScene(scene);
		}
	}
	
	@FXML
	private void onPlayBtnClicked(ActionEvent event) throws Exception {
		if (categoryTV.getSelectionModel().getSelectedItem() == null) {
			showAlert("You haven't selected a category");
		};
		Thread worker = new Thread(() -> {
			String command = String.format("wikit %s", "apple");
			List<String> output;
			try {
				output = ShellHelper.execute(command);
				String wikitText = output.get(0);
				wikitText = wikitText.toLowerCase();
				wikitText = wikitText.replaceAll("apple", "something");
				MediaHelper mh = new MediaHelper("./.Game/");
				mh.createAudioChunk(wikitText, "kal_diphone", "temp");
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Platform.runLater(()->{
				Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
				Scene scene;
				try {
					scene = new Scene(SceneSwitcher.getGameAudioPlayer("apple"));
					stage.setScene(scene);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		});
		worker.start();
	}
	
	private void showAlert(String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Error");
		alert.setHeaderText("Can't proceed");
		alert.setContentText(text);
		alert.showAndWait();
	}
	
}
