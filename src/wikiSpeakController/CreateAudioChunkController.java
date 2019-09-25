package wikiSpeakController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import wikiSpeak.ShellHelper;

public class CreateAudioChunkController {
	@FXML
	private TextArea selectedTextTA;
	
	@FXML
	private ComboBox<String> voiceCombo;
	
	@FXML
    private void initialize() {
		voiceCombo.getItems().addAll(
		    "akl_nz_jdt_diphone",
		    "akl_nz_cw_cg_cg"
		);
    }
	
	@FXML
    private void previewBtnClicked() {
		String text = selectedTextTA.getText();
		String voiceOption = voiceCombo.getValue();
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > temp.txt", text);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o temp.wav -eval \'(voice_%s)\' < temp.txt",
						voiceOption);
				ShellHelper.execute(command);
				command = "play ./temp.wav";
				ShellHelper.execute(command);
				command = "rm temp.wav temp.txt";
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		worker.start();
    }
	
	@FXML
    private void saveBtnClicked() {
		String text = selectedTextTA.getText();
		String voiceOption = voiceCombo.getValue();
		
		Thread worker = new Thread(()->{
			String command = String.format("echo \"%s\" > temp.txt", text);
			try {
				ShellHelper.execute(command);
				command = String.format("text2wave -o \"$(date --iso-8601=seconds).wav\" -eval \'(voice_%s)\' < temp.txt",
						voiceOption);
				ShellHelper.execute(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		worker.start();
    }
	
	public void setText(String text) {
		selectedTextTA.setText(text);
	}
}
