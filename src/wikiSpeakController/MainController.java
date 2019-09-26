package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wikiSpeak.Main;
import wikiSpeak.Search;
import wikiSpeak.ViewCreations;

public class MainController {
	@FXML
	private void onViewBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(ViewCreations.getLayout());
		stage.setScene(scene);
	}
	
	@FXML
	private void onCreateBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(Search.getLayout());
		stage.setScene(scene);
	}
	
//	@FXML
//	private void onViewBtnClicked(ActionEvent event) throws IOException {
//		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
//		Scene scene = new Scene(Main.getLayout());
//		stage.setScene(ViewCreationsUI.getInstance(()->stage.setScene(scene)));
//	}
}
