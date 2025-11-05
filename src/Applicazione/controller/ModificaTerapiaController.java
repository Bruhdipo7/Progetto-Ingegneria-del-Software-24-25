package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import application.Amministratore;
import application.Database;
import application.Navigator;
import application.Sessione;
import application.model.Terapia;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ModificaTerapiaController {
	
	private Utente u;
	private Utente p;
	private Terapia t;
	
	// FIELD
	@FXML private TextField farmacoField;
	@FXML private TextField dosiGiornaliereField;
	@FXML private TextField quantitàField;
	@FXML private DatePicker dataInizioField;
	@FXML private DatePicker dataFineField;
	@FXML private TextArea indicazioniField;
	
	// LABEL
	@FXML private Label labelPaziente;
	@FXML private Label nomeFarmacoLabel;

	// VARIABILI
	int dosiGiornaliere;
	int quantità;
	LocalDate dataInizio;
	LocalDate dataFine;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		t = Sessione.getInstance().getTerapiaSelezionata();
		
		labelPaziente.setText(p.getNomeCognome() + " (" + p.getCf() + ")");
		nomeFarmacoLabel.setText(t.getNomeFarmaco());
	}
	
	@FXML
	private void handleModificaTerapia(ActionEvent event) throws IOException {
		
		try {
	        dosiGiornaliere = Integer.parseInt(dosiGiornaliereField.getText());
	        quantità = Integer.parseInt(quantitàField.getText());
	        dataInizio = dataInizioField.getValue();
	        dataFine = dataFineField.getValue();

	        if (dosiGiornaliere < 1 || quantità < 1 ||
	            dataInizio.isBefore(LocalDate.now()) ||
	            dataFine.isBefore(dataInizio) ||
	            dataFine.isEqual(dataInizio) ||
	            dataFine.isBefore(LocalDate.now())) {

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
		
		// LISTA TERAPIA IN CONFLITTO
		List<Terapia> conflitti = Amministratore.terapie.stream()
			.filter(terapia -> terapia.getCf().equals(p.getCf())
					&& !terapia.getNomeFarmaco().equals(t.getNomeFarmaco())
					&& !terapia.getDataInizio().equals(dataInizioField.getValue()))
			.filter(terapia -> {
					LocalDate inizio = terapia.getDataInizio();
					LocalDate fine = terapia.getDataFine();
					
					return (dataInizioField.getValue().isAfter(inizio) && dataInizioField.getValue().isBefore(fine) || 
							dataInizioField.getValue().isEqual(fine) || dataInizioField.getValue().isEqual(inizio)) || 
						   (dataFineField.getValue().isBefore(fine) && dataFineField.getValue().isAfter(inizio) || 
								   dataFineField.getValue().isEqual(fine) || dataFineField.getValue().isEqual(inizio)) ||
						   (dataInizioField.getValue().isBefore(inizio) && dataFineField.getValue().isAfter(fine));
			})
			.collect(Collectors.toList());
		
		if(!conflitti.isEmpty()) {
			StringBuilder msg = new StringBuilder("Terapie in conflitto:\n");
			conflitti.forEach(terapia ->
					msg.append("- ").append(terapia.getNomeFarmaco()).append(": ")
					   .append(terapia.getDataInizio()).append(" -> ")
					   .append(terapia.getDataFine()).append("\n")
			);
			
			try {
		        Amministratore.msgController.showErrorWindow(msg.toString());
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			return;
		}
	    
		String query = "UPDATE terapie SET dosiGiornaliere = ?, quantità = ?, dataInizio = ?, dataFine = ?, indicazioni = ?, modificato = ? WHERE id = ?";
		try (Connection conn = Database.getConnection(); 
			PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, dosiGiornaliere);
	        stmt.setInt(2, quantità);
	        stmt.setDate(3, java.sql.Date.valueOf(dataInizio));
	        stmt.setDate(4, java.sql.Date.valueOf(dataFine));
	        stmt.setString(5, indicazioniField.getText());
	        stmt.setString(6, u.getNomeCognome());
	        stmt.setInt(7, t.getId());
	        
	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
	        		Amministratore.loadTerapieFromDatabase();
	        		Amministratore.msgController.showSuccessWindow("Terapia modificata correttamente.");
	            switchToMostraDatiPaziente(event);
	        } else {
	        		Amministratore.msgController.showErrorWindow("Errore nell'inserimento della terapia.");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Sessione.getInstance().nullTerapiaSelezionata();
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}