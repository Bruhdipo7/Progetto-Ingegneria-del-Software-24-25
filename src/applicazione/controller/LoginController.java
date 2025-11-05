package controller;

import java.io.IOException;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

	@FXML private TextField cfField;
	@FXML private PasswordField passwordField;
	@FXML private Label firstLabel;
	
	@FXML
	private void initialize() {
		firstLabel.setFocusTraversable(true);
	}
	
	@FXML 
	private void handleLogin(ActionEvent event) throws IOException {
		
		if(cfField.getText().isBlank() || passwordField.getText().isBlank()) {
			Amministratore.msgController.showErrorWindow("Inserire codice fiscale e password.");
			return;
		}
		
		Utente utente;
		if(Amministratore.utenteEsiste(cfField.getText()))
			utente = Amministratore.getUtente(cfField.getText());
		else {
			Amministratore.msgController.showErrorWindow("Utente non esistente.");
			return;
		}
		
		if(utente.checkPw(passwordField.getText())) {
			Sessione.getInstance().setUtente(utente);
			
			if(utente.getRuolo().equals("diabetologo")) {
				Navigator.getInstance().switchToDiabetologoPage(event);
			}
			else if(utente.getRuolo().equals("paziente")) {
				Navigator.getInstance().switchToPazientePage(event);
			}
		}
		else
			Amministratore.msgController.showErrorWindow("Codice fiscale o password errati.");
	}
}
