package wikiSpeakController;

import java.io.IOException;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wikiSpeak.ViewCreationsUI;
import wikiSpeak.Main;

public class MainController {
	@FXML
	private void onViewBtnClicked(ActionEvent event) throws IOException {
		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(Main.getLayout());
		stage.setScene(ViewCreationsUI.getInstance(()->stage.setScene(scene)));
	}
	
//	@FXML
//	private void onViewBtnClicked(ActionEvent event) throws IOException {
//		Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
//		Scene scene = new Scene(Main.getLayout());
//		stage.setScene(ViewCreationsUI.getInstance(()->stage.setScene(scene)));
//	}
}
