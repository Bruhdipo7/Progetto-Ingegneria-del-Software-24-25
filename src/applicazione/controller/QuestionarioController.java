package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import application.Amministratore;
import application.Database;
import application.Navigator;
import application.Sessione;
import application.model.Utente;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class QuestionarioController {

	private Utente u;
	
	//TEXTFIELD
	@FXML private TextField nomeFarmacoField;
	@FXML private TextField dosiGiornaliereField;
	@FXML private TextField quantitàField;
	@FXML private TextArea sintomiArea;
	
	int dosiGiornaliere;
	int quantità;
	String sintomi;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
	}
	
	@FXML
	private void handleQuestionario(ActionEvent event) throws IOException {
		
		try {
			nomeFarmacoField.getText().isBlank();
	        dosiGiornaliere = Integer.parseInt(dosiGiornaliereField.getText());
	        quantità = Integer.parseInt(quantitàField.getText());

	        if (dosiGiornaliere < 1 || quantità < 1) {
	            Amministratore.msgController.showErrorWindow("Per favore compila tutti i campi correttamente.");
	            return;
	        }

	    } catch (NullPointerException n) {
	    	Amministratore.msgController.showErrorWindow("Per favore compila tutti i campi obbligatori.");
	        return;
	    } catch (NumberFormatException n) {
	        Amministratore.msgController.showErrorWindow("Per favore inserisci solo numeri nei campi numerici.");
	        return;
	    }
		
		sintomi = sintomiArea.getText();
		
		String query = "INSERT INTO questionario (CF, giornoCompilazione, nomeFarmaco, dosiGiornaliere, quantità, sintomi, controllato) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    	try (Connection conn = Database.getConnection(); 
	    		PreparedStatement stmt = conn.prepareStatement(query)) {
	
	    		stmt.setString(1, u.getCf());
	    		stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
	    		stmt.setString(3, nomeFarmacoField.getText());
	    		stmt.setInt(4, dosiGiornaliere);
	    	    stmt.setInt(5, quantità);
	    	    stmt.setString(6, sintomi);
	    	    stmt.setBoolean(7, false);

	    	    int rows = stmt.executeUpdate();

	    	    if (rows > 0) {
	        		Amministratore.loadQuestionarioFromDatabase();
	        		Amministratore.msgController.showSuccessWindow("Questionario compilato.");
	        		switchToPazientePage(event);
	    	    } else {
	    	    	Amministratore.msgController.showErrorWindow("Errore nell'inserimento della terapia.");
	    	    }

    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToPazientePage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToPazientePage(event);
	}
}