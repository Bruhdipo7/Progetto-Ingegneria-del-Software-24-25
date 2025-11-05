package controller;

import java.io.IOException;

import application.Navigator;
import application.Sessione;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ProfiloDiabetologoController {

	private Utente u;
	
	// LABEL - PROFILO
	@FXML private Label labelProfilo;
	@FXML private Label nomeCognomeLabel;
	@FXML private Label dateOfBirthLabel;
	@FXML private Label sexLabel;
	@FXML private ImageView fotoProfiloImage;
		
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		
		labelProfilo.setText("Profilo di " + u.getNomeCognome());
		nomeCognomeLabel.setText("Nome e cognome: " + u.getNomeCognome());
		dateOfBirthLabel.setText("Data di nascita: " + u.getDataDiNascita());
		sexLabel.setText("Sesso: " + u.getSesso());
		
		//File file = new File(u.getPath());
		//Image image = new Image(file.toURI().toString());
		//fotoProfiloImage.setImage(image);
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToDiabetologoPage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToDiabetologoPage(event);
	}
}
