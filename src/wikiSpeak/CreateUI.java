package wikiSpeak;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.util.converter.NumberStringConverter;

/***
 * Singleton class to represent the UI for making a creation
 * @author Xiaobin Lin
 *
 */
public class CreateUI {
	private TextField _textInput;
	private Button _searchBtn;
	private ListView<String> _wikitContents;
	private TextField _lineNumberInput;
	private Button _createBtn;
	private TextField _creationNameInput;
	private Button _backBtn;
	private static Scene _scene = null;

	public static final String contentPlaceHolder = "Please use the input field above to search";
	public static final String loadingText = "Loading...";

	

	private CreateUI(Runnable onBack) {
		// Initialize UI components
		_textInput = new TextField();
		_textInput.setPromptText("Searching...");
		_searchBtn = new Button("Search");
		_createBtn = new Button("Create");
		_backBtn = new Button("Back");
		_backBtn.setOnAction(event -> {onBack.run();});
		_wikitContents = new ListView<>();
		_lineNumberInput = new TextField();
		_creationNameInput = new TextField();

		// Set grid UI
		GridPane layout = new GridPane();
		layout.setPadding(new Insets(10,10,10,10));
		layout.getColumnConstraints().add(new ColumnConstraints(780));
		layout.getColumnConstraints().add(new ColumnConstraints(100));
		layout.getColumnConstraints().add(new ColumnConstraints(100));

		// Force the input field to take only numbers
		_lineNumberInput.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
		_wikitContents.setPlaceholder(new Label(contentPlaceHolder));
		_lineNumberInput.setPromptText("Line Number");
		_creationNameInput.setPromptText("Creation Name");

		// Bind UI Button to action
		_searchBtn.setOnAction(event -> onSearch());
		_createBtn.setOnAction(event -> onCreate());

		// Add/set UI
		layout.setGridLinesVisible(true);
		layout.add(buttonWrapper(_backBtn), 0, 0,3, 1);
		layout.add(_textInput, 0, 1, 1, 1);
		layout.add(buttonWrapper(_searchBtn), 1, 1, 2, 1);
		layout.add(_wikitContents, 0, 3, 3, 1);
		layout.add(_creationNameInput, 0, 4, 1, 1);
		layout.add(_lineNumberInput, 1, 4, 1, 1);
		layout.add(buttonWrapper(_createBtn), 2, 4, 1, 1);
		_scene = new Scene(layout, 1000, 600);
	}
	
	/***
	 * Validate user input and create the creation if inputs are valid
	 */
	private void onCreate() {
		int userLN = validLN();
		if (userLN == -1){
			showErrorAlert("Invalid line number");
			return;
		}

		String creationName = _creationNameInput.getText();
		if (!validCreationName(creationName)){
			showErrorAlert("Invalid creation name, please make sure there are no spaces");
			return;
		}
		if (fileExists(creationName)) {
			showErrorAlert("Creation name " + creationName + " already exists, please rename");
			return;
		}

		// Set disable button to indicate creation in progress
		_createBtn.setDisable(true);
		Thread worker = new Thread(() -> {
			// Make the creation
			createCreation(userLN, creationName);
		});
		worker.start();
	}

	/***
	 * Use wikit to search the Internet and store the result in wikitContent table with column numbers
	 */
	private void onSearch(){
		_wikitContents.setPlaceholder(new Label(loadingText));
		Thread worker = new Thread(() -> {
			try{
				String command = String.format("wikit %s", _textInput.getText());
				List<String> output = ShellHelper.execute(command);
				if (output.size() == 0 || output.get(0).contains("not found :^(")) {
					// Show alert and exit
					Platform.runLater(() -> {
						_wikitContents.setPlaceholder(new Label(contentPlaceHolder));
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText(String.format("%s: Nothing found :(", _textInput.getText()));
						resetCreate();
						alert.showAndWait();
					});
					return;
				}
				String wikiText = output.get(0);
				command = String.format("echo \"%s\" | grep -Po '[^\\s].+?\\.'", wikiText);
				List<String> wikiTextList = ShellHelper.execute(command);

				Platform.runLater(() -> {
					// Update wikitContents table to show wikit text
					_wikitContents.getItems().clear();
					for (int i = 1; i <= wikiTextList.size(); i++) {
						_wikitContents.getItems().add(String.format("%02d %s", i, wikiTextList.get(i-1)));
					}
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					_wikitContents.setPlaceholder(new Label(contentPlaceHolder));
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Invalid search, please search with a different word");
					alert.showAndWait();
				});
			}
		});
		worker.start();
	}

	/***
	 * Make the creation using bash, ffmpeg, text2wav and others and store it in ./Creations
	 * @param userLN
	 * @param creationName
	 */
	private void createCreation(int userLN, String creationName){
		String textToSpeak = "";
		for (int i = 1; i <= userLN; i++) {
			// Add the line of text user specified, excluding the leading digits
			textToSpeak += _wikitContents.getItems().get(i-1).substring(3);
		}
		try {
			// Create the wav audio file
			String command = String.format("echo \"%s\" | grep -Po '[^\\s].+?\\.' | " +
							"head -%s | text2wave -o ./Creations/%s\"_temp\".wav",
					textToSpeak, userLN, creationName);
			ShellHelper.execute(command);

			// Get the duration of the audio file
			command = String.format("soxi -D ./Creations/%s_temp.wav", creationName);
			int audioDuration = ((int) Double.parseDouble(ShellHelper.execute(command).get(0))) + 1;

			// Create a video with user input text
			command = String.format("ffmpeg -f lavfi -i color=c=blue"
					+ ":s=320x240:d=%s -vf \"drawtext=fontf"
					+ "ile=./COMIC.TTF:fontsize=30: fontcolor=white:"
					+ "x=(w-text_w)/2:y=(h-text_h)/2:t"
					+ "ext='%s'\" "
					+ "./Creations/%s\"_temp\".mp4", audioDuration, _textInput.getText(), creationName);
			ShellHelper.execute(command);

			// Merge video and audio to make one video
			command = String.format("ffmpeg -i ./Creations/%s\"_temp\".mp4 -i "
					+ "./Creations/%s\"_temp\".wav "
					+ "-vcodec copy ./Creations/%s.mp4", creationName, creationName, creationName);
			ShellHelper.execute(command);

			// Remove temp audio and temp video
			command = String.format("rm ./Creations/%s\"_temp\".wav " +
					"./Creations/%s\"_temp\".mp4\n", creationName, creationName);
			ShellHelper.execute(command);

			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText(String.format("Creation %s created", creationName));
				alert.showAndWait();
				_createBtn.setDisable(false);
				resetCreate();
				_wikitContents.setPlaceholder(new Label(contentPlaceHolder));
			});
		} catch (Exception e) {
			Platform.runLater(() -> {
				showErrorAlert("Failed to create");
				resetCreate();
			});
		}
	}


	/***
	 * Static method to create instance of Singleton class
	 * @param onBack
	 * @return
	 */
	public static Scene getInstance(Runnable onBack)
	{
		if (_scene == null){
			new CreateUI(onBack);
		}

		return _scene;
	}

	/***
	 * A wrapper so that the button can take the full width
	 * @param button
	 * @return
	 */
	public HBox buttonWrapper(Button button){
		HBox hbox = new HBox();
		hbox.getChildren().add(button);
		HBox.setHgrow(button, Priority.ALWAYS);
		button.setMaxWidth(Double.MAX_VALUE);
		return hbox;
	}

	/***
	 * Method to check if creation exists in ./Creations
	 * @param filename
	 * @return
	 */
	public static boolean fileExists(String filename) {
		String command = String.format("test -f \"./Creations/%s.mp4\"", filename);
		try {
			ShellHelper.execute(command);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/***
	 * Reset the input fields to get ready for next input
	 */
	public void resetCreate(){
		_textInput.clear();
		_wikitContents.getItems().clear();
		_lineNumberInput.clear();
		_creationNameInput.clear();
	}

	/***
	 * Validate user supplied line number
	 * @return
	 */
	private int validLN(){
		
		int inputLN = 0;
		try {
			inputLN = Integer.parseInt(_lineNumberInput.getText());
		} catch (NumberFormatException e) {
			inputLN = 0;
		}
		int ln = _wikitContents.getItems().size();
		if (inputLN > ln || inputLN <= 0) {
			return -1;
		}
		return inputLN;
	}

	/***
	 * Validate user supplied creation name
	 * @param name
	 * @return
	 */
	private boolean validCreationName(String name){
		// Creation name cannot be empty or contain white spaces
		if (name.equals("") || (name.indexOf(" ") != -1)){
			return false;
		}
		return true;
	}

	/***
	 * Show UI alert dialog
	 * @param message
	 */
	private void showErrorAlert(String message){
		Alert alert = new Alert(AlertType.ERROR);
		Label label = new Label(message);
		label.setWrapText(true);
		alert.getDialogPane().setContent(label);
		alert.showAndWait();
	}
}
