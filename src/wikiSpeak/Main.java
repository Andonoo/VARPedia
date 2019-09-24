package wikiSpeak;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import wikiSpeakController.VideoPlayerController;

public class Main extends Application{
//	Stage _primaryStage;
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		_primaryStage = primaryStage;
//		
//		// Initialize the creation folder
//		String command = "mkdir -p ./Creations";
//        ShellHelper.execute(command);
//
//        // UI elements for this scene
//		GridPane layout = new GridPane();
//		Button viewBtn = new Button("View existing Creations");
//		Button playBtn = new Button("Make a new Creation");
//		Label welcomeText = new Label("Wiki search");
//		Label hintText = new Label("Please select the options below");
//		
//		// Change the font of welcomeText and hintText
//		welcomeText.setFont(Font.font("Cambria", 32));
//		welcomeText.setAlignment(Pos.CENTER);
//		GridPane.setFillWidth(welcomeText, true);
//		hintText.setFont(Font.font("Cambria", 13));
//
//		// Make sure the welcome text is justified
//		ColumnConstraints firstCol = new ColumnConstraints();
//		firstCol.setHalignment(HPos.CENTER);
//		layout.getColumnConstraints().add(firstCol);
//		
//		// Add and modify UI
//		layout.setAlignment(Pos.CENTER);
//		layout.setPadding(new Insets(10,10,10,10));
//		layout.setVgap(10);
//		layout.add(welcomeText, 0, 0,1, 1);
//		layout.add(hintText, 0, 1, 1, 1);
//		layout.add(buttonWrapper(playBtn), 0, 2, 1, 1);
//		layout.add(buttonWrapper(viewBtn), 0, 3,1, 1);
//		
//		Scene menu = new Scene(layout, 1000, 600);
//
//		primaryStage.setTitle("Wiki Speak");
//
//		// Bind change scene to buttons
//		viewBtn.setOnAction(event -> {
//			primaryStage.setScene(ViewCreationsUI.getInstance(() -> primaryStage.setScene(menu)));
//		});
//
//		playBtn.setOnAction(event -> {
//			primaryStage.setScene(CreateUI.getInstance(()->primaryStage.setScene(menu)));
//		});
//		
//		// Display UI
//		primaryStage.setScene(menu);
//		primaryStage.show();
//	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(getLayout());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static Parent getLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("Main.fxml"));
		Parent layout = loader.load();
		return layout;
	}

	/***
	 * // A wrapper so that the button can take the full width
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
	
	public static void main(String args[]) {
		launch(args);
	}

	/**
	 * THE FOLLOWING DEMONSTRATES HOW WE CAN PASS PARAMETERS TO THE CONTROLLER OBJECTS
	 */
	
//	 @Override
//	 public void start(Stage primaryStage) throws Exception {
//	 	FXMLLoader loader = new FXMLLoader();
//	 	loader.setLocation(Main.class.getResource("PlayerUI.fxml"));
//	 	Parent layout = loader.load();
//	 	Scene scene = new Scene(layout);
//	 	VideoPlayerController controller = loader.<VideoPlayerController>getController();
//	 	controller.setVideo("Apple");
//	 	primaryStage.setScene(scene);
//	 	primaryStage.show();
//	 }
//	
//	 public static void main(String args[]) {
//	 	launch(args);
//	 }
}
