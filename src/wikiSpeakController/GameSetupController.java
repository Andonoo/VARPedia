package wikiSpeakController;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeakController.SceneSwitcher.SceneOption;
import wikiSpeakModel.GameCategory;
import wikiSpeakModel.GuessMedia;
import wikiSpeakModel.GuessingGameEngine;
import wikiSpeakModel.MediaType;

public class GameSetupController extends Navigation{
	@FXML Spinner<Integer> _ageSpinner;
	@FXML Slider _nOfGameSlider;
	@FXML TableView<String> _categoryTV;
	@FXML TableColumn<String, String> _categoryCol;
	@FXML RadioButton _textRB;
	@FXML RadioButton _audioRB;
	@FXML RadioButton _videoRB;
	@FXML Button _playBtn;
	@FXML TextField _nameTF;
	@FXML ToggleButton _discoveryTB;
	
	Tooltip _discoveryToolTip;
	ToggleGroup _radioButtonGroup;
	
	/**
	 * Setup GUI elements
	 */
	@FXML
	private void initialize() {
		loadCategoryTable();
		_ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15));
		// Toggle group ensures only one is selected at a time
		_radioButtonGroup = new ToggleGroup();
		_textRB.setToggleGroup(_radioButtonGroup);
		_audioRB.setToggleGroup(_radioButtonGroup);
		_videoRB.setToggleGroup(_radioButtonGroup);
		_videoRB.setSelected(true);
		_categoryTV.setVisible(false);
		_discoveryTB.setTooltip(new Tooltip("Hi"));
	}
	
	/***
	 * Load category names into table.
	 */
	private void loadCategoryTable() {
		ObservableList<String> categories = FXCollections.observableArrayList("Fruits", "Animals", "Countries", "Celebrities");
		_categoryCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
		_categoryTV.setItems(categories);
		_categoryTV.getSelectionModel().selectFirst();_categoryTV.setVisible(false);
	}
	
	/**
	 * Attempts to create a game session.
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void onPlayBtnClicked(ActionEvent event) throws Exception {
		MediaType mediaType = this.getMediaType();
		GameCategory gameCategory;
		if (_categoryTV.getSelectionModel().getSelectedItem() == null && _discoveryTB.isSelected()) {
			showAlert("You haven't selected a category");
			return;
		} else {
			gameCategory = this.getGameCategory();
		}
		_playBtn.setDisable(true);
		_playBtn.setText("Wait...");
		String name = _nameTF.getText();
		int age = _ageSpinner.getValue();
		int nOfGames = (int) _nOfGameSlider.getValue();
		
		GuessingGameEngine engine = new GuessingGameEngine(name, age, nOfGames, gameCategory, mediaType);
		Thread worker = new Thread(() -> {
			try {
				engine.prepareGame();
				Platform.runLater(()->{
					Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(Main.class.getResource("GameStage.fxml"));
					Parent layout;
					try {
						layout = loader.load();
						GameStageController controller = loader.<GameStageController>getController();
						controller.setEngine(engine);
						Scene scene = new Scene(layout);
						stage.setScene(scene);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e1) {
				Platform.runLater(()->{
					showAlert(e1.getMessage());
				});
			}
		}); 
		worker.start();
	}
	
	/**
	 * Get MediaType Enum from input
	 * @return
	 * @throws Exception
	 */
	private MediaType getMediaType() throws Exception{
		RadioButton selectedRadioButton = (RadioButton) _radioButtonGroup.getSelectedToggle();
		String radioBtnText = selectedRadioButton.getText();
		switch (radioBtnText) {
			case ("Slideshow"):
				return MediaType.Video;
			case ("Audio"):
				return MediaType.Audio;
			case ("Text"):
				return MediaType.Text;
		}
		throw new Exception("Wrong MediaType");
	}
	
	/**
	 * Get the game category Enum from input
	 * @return
	 * @throws Exception
	 */
	private GameCategory getGameCategory() throws Exception{
		if (!_discoveryTB.isSelected()) {
			return GameCategory.Creations;
		}
		String category = _categoryTV.getSelectionModel().getSelectedItem();
		switch (category) {
			case ("Animals"):
				return GameCategory.Animals;
			case ("Celebrities"):
				return GameCategory.Celebrities;
			case ("Countries"):
				return GameCategory.Countries;
			case ("Fruits"):
				return GameCategory.Fruits;
		}
		throw new Exception("Wrong GameType");
	}
	
	@FXML
	private void discoveryOnToggle(ActionEvent event) {
		if (_discoveryTB.isSelected()) {
			_discoveryTB.setText("Discovery Mode");
			_categoryTV.setVisible(true);
		} else {
			_discoveryTB.setText("Review Mode");
			_categoryTV.setVisible(false);
		}
	}
	
	@FXML
	private void helpOnClicked(ActionEvent event) {
		showInfo("Review Mode is for review the creations you've made\n"
				+ "Discovery Mode is for learning different terms you haven't seen before, "
				+ "the difficulty varies based on your age!");
	}
}
