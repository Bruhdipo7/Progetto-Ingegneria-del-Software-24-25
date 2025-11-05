package controller;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MessageController {

	@FXML private Label statusLabel;
	
	public void setMessage(String message) {
		statusLabel.setText(message);
	}
	
	public static void show(String message, String title, int secondi) throws IOException {
        FXMLLoader loader = new FXMLLoader(MessageController.class.getResource("/fxml/Message.fxml"));
        Parent root = loader.load();

        MessageController controller = loader.getController();
        controller.setMessage(message);

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();

        // Chiudi automaticamente dopo X secondi
        PauseTransition delay = new PauseTransition(Duration.seconds(secondi));
        delay.setOnFinished(_ -> stage.close());
        delay.play();
	}
	
	public void showErrorWindow(String message) throws IOException {
		show(message, "Errore", 3);
	}
	
	public void showSuccessWindow(String message) throws IOException {
		show(message, "Successo", 1);
	}
}
