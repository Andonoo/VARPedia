package wikiSpeakController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GameSetupController {
	@FXML Spinner<Integer> _ageSpinner;
	@FXML Spinner<Integer> _nOfGamesSpinner;
	@FXML TableView<String> _categoryTV;
	@FXML TableColumn<String, String> _categoryCol;
	@FXML RadioButton _textRB;
	@FXML RadioButton _audioRB;
	@FXML RadioButton _videoRB;
	@FXML Button _playBtn;
	
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void onHomeBtnClicked() {
		
	}
	
	@FXML
	private void onPlayBtnClicked() {
		
	}
	
}
