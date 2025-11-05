package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import application.Amministratore;
import application.Database;
import application.Navigator;
import application.Sessione;
import application.model.FattoriComorbiditàAllergie;
import application.model.Patologia;
import application.model.TerapiaConcomitante;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class StoriaDatiPazienteController {
	
	private Utente u;
	private Utente p;
	
	@FXML private ComboBox<String> tipologia;
	@FXML private TextField nomeField;
	@FXML private TextField nomePatologiaField;
	@FXML private DatePicker dataPatologiaField;
	@FXML private TextArea indicazioniPatologiaArea;
	@FXML private TextField nomeTerapiaField;
	@FXML private DatePicker dataInizioTerapiaField;
	@FXML private DatePicker dataFineTerapiaField;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		
		tipologia.getItems().addAll("Fattore Di Rischio", "Comorbidità", "Allergia");
	}
	
	// GESTIONE FATTORI DI RISCHIO E COMORBIDITÀ
	@FXML
	private void aggiungiFattoreComorbiditàAllergia(ActionEvent event) throws IOException { 

		if(nomeField.getText() == null || nomeField.getText().isBlank() || tipologia.getValue() == null) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
			return;
		}
	
		if (tipologia.getValue().equals("Fattore Di Rischio") && 
				Amministratore.fattoriDiRischio.stream()
					.anyMatch(f -> f.getNome().equalsIgnoreCase(nomeField.getText()) && f.getCF().equals(p.getCf()))) {
			Amministratore.msgController.showErrorWindow("Fattore di rischio già presente.");
			return;
		}
		
		if (tipologia.getValue().equals("Comorbidità") && 
				Amministratore.comorbidità.stream()
					.anyMatch(c -> c.getNome().equalsIgnoreCase(nomeField.getText()) && c.getCF().equals(p.getCf()))) {
			Amministratore.msgController.showErrorWindow("Comorbidità già presente.");
			return;
		}
		
		if (tipologia.getValue().equals("Allergia") && 
				Amministratore.allergie.stream()
					.anyMatch(a -> a.getNome().equalsIgnoreCase(nomeField.getText()) && a.getCF().equals(p.getCf()))) {
			Amministratore.msgController.showErrorWindow("Allergia già presente.");
			return;
		}
		
		String query = "INSERT INTO fattoricomorbiditàallergie (CF, tipo, nome, modificato) VALUES (?, ?, ?, ?)";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, tipologia.getValue());
				stmt.setString(3, nomeField.getText());
				stmt.setString(4, u.getCf());
						
				FattoriComorbiditàAllergie storiaPaziente = new FattoriComorbiditàAllergie(p.getCf(), nomeField.getText(), u.getCf());
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					if(tipologia.getValue().equals("Fattore Di Rischio"))
						Amministratore.fattoriDiRischio.add(storiaPaziente);
					else if(tipologia.getValue().equals("Comorbidità"))
						Amministratore.comorbidità.add(storiaPaziente);
					else if(tipologia.getValue().equals("Allergia"))
						Amministratore.allergie.add(storiaPaziente);
					
					Amministratore.loadFattoriComorbiditàAllergieFromDatabase();
					Amministratore.msgController.showSuccessWindow("Dato paziente inserito.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nell'inserimento del dato.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}	
	}
	
	@FXML
	private void rimuoviFattoreComorbiditàAllergia(ActionEvent event) throws IOException {
		
		if(nomeField.getText() == null || nomeField.getText().isBlank() || tipologia.getValue() == null) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
		} else {
			String query = "DELETE FROM fattoricomorbiditàallergia WHERE CF = ? AND tipo = ? AND nome = ?";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, tipologia.getValue());
				stmt.setString(3, nomeField.getText());
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					Amministratore.loadFattoriComorbiditàAllergieFromDatabase();
					Amministratore.msgController.showSuccessWindow("Dato paziente rimosso.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nella rimozione del dato.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// GESTIONE PATOLOGIE PREGRESSE
	@FXML
	private void aggiungiPatologia(ActionEvent event) throws IOException {
		
		if (Amministratore.patologie.stream()
				.anyMatch(patologia -> patologia.getNome().equalsIgnoreCase(nomePatologiaField.getText()) && patologia.getCf().equals(p.getCf()))) {
			Amministratore.msgController.showErrorWindow("Patologia già presente.");
			return;
		}
		
		if(dataPatologiaField.getValue() == null) {
			Amministratore.msgController.showErrorWindow("Inserire tutti i dati.");
			return;
		}
		
		if(nomePatologiaField.getText() == null || nomePatologiaField.getText().isBlank() || 
				indicazioniPatologiaArea.getText() == null || indicazioniPatologiaArea.getText().isBlank() ||
				dataPatologiaField.getValue().isAfter(LocalDate.now())) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
			return;
		}

		String query = "INSERT INTO patologie (CF, nome, dataInizio, stato, modificato) VALUES (?, ?, ?, ?, ?)";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, nomePatologiaField.getText());
				stmt.setDate(3, java.sql.Date.valueOf(dataPatologiaField.getValue()));
				stmt.setString(4, indicazioniPatologiaArea.getText());
				stmt.setString(5, u.getCf());
						
				Patologia patologia = new Patologia(
						p.getCf(),
						nomePatologiaField.getText(),
						dataPatologiaField.getValue(),
						indicazioniPatologiaArea.getText(),
						u.getCf()
					);
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					Amministratore.patologie.add(patologia);
					Amministratore.loadPatologieFromDatabase();
					Amministratore.msgController.showSuccessWindow("Dato paziente inserito.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nell'inserimento del dato.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	@FXML
	private void rimuoviPatologia(ActionEvent event) throws IOException {
		
		if(nomePatologiaField.getText() == null || nomePatologiaField.getText().isBlank() || 
				indicazioniPatologiaArea.getText() == null || indicazioniPatologiaArea.getText().isBlank() ||
				dataPatologiaField.getValue().isAfter(LocalDate.now())) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
			return;
		}
		
		String query = "DELETE FROM patologie WHERE CF = ? AND nome = ?";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, nomePatologiaField.getText());
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					Amministratore.loadPatologieFromDatabase();
					Amministratore.msgController.showSuccessWindow("Patologia paziente rimossa.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nella rimozione della patologia.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	// GESTIONE TERAPIE CONCOMITANTI
	@FXML
	private void aggiungiTerapia(ActionEvent event) throws IOException {
		
		if (Amministratore.terapieConcomitanti.stream()
				.anyMatch(tc -> tc.getNome().equalsIgnoreCase(nomeTerapiaField.getText()) 
							&& tc.getDataInizio().equals(dataInizioTerapiaField.getValue())
							&& tc.getCf().equals(p.getCf()))) {
			Amministratore.msgController.showErrorWindow("Terapia concomitante già presente.");
			return;
		}
		
		if(dataInizioTerapiaField.getValue() == null || dataFineTerapiaField.getValue() == null) {
			Amministratore.msgController.showErrorWindow("Inserire tutti i dati.");
			return;
		}
			
		if(nomeTerapiaField.getText() == null || nomeTerapiaField.getText().isBlank() ||
				dataFineTerapiaField.getValue().isBefore(dataInizioTerapiaField.getValue())) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
			return;
		}

		String query = "INSERT INTO terapieconcomitanti (CF, nome, dataInizio, dataFine, modificato) VALUES (?, ?, ?, ?, ?)";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, nomeTerapiaField.getText());
				stmt.setDate(3, java.sql.Date.valueOf(dataInizioTerapiaField.getValue()));
				stmt.setDate(4, java.sql.Date.valueOf(dataFineTerapiaField.getValue()));
				stmt.setString(5, u.getCf());
						
				TerapiaConcomitante terapiaConcomitante = new TerapiaConcomitante(
						p.getCf(),
						nomeTerapiaField.getText(),
						dataInizioTerapiaField.getValue(),
						dataFineTerapiaField.getValue(),
						u.getCf()
					);
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					Amministratore.terapieConcomitanti.add(terapiaConcomitante);
					Amministratore.loadTerapieConcomitantiFromDatabase();
					Amministratore.msgController.showSuccessWindow("Terapia concomitante inserita.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nell'inserimento della terapia concomitante.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	@FXML
	private void rimuoviTerapia(ActionEvent event) throws IOException {
		
		if(dataInizioTerapiaField.getValue() == null || dataFineTerapiaField.getValue() == null) {
			Amministratore.msgController.showErrorWindow("Inserire tutti i dati.");
			return;
		}
			
		if(nomeTerapiaField.getText() == null || nomeTerapiaField.getText().isBlank() ||
				dataFineTerapiaField.getValue().isBefore(dataInizioTerapiaField.getValue())) {
			Amministratore.msgController.showErrorWindow("Inserire i dati correttamente.");
			return;
		}
		
		String query = "DELETE FROM terapieconcomitanti WHERE CF = ? AND nome = ?";
			try(Connection conn = Database.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
						
				stmt.setString(1, p.getCf());
				stmt.setString(2, nomeTerapiaField.getText());
						
				int rows = stmt.executeUpdate();
						
				if(rows > 0) {
					Amministratore.loadTerapieConcomitantiFromDatabase();
					Amministratore.msgController.showSuccessWindow("Terapia concomitante rimossa.");
					switchToMostraDatiPaziente(event);
				} else {
					Amministratore.msgController.showErrorWindow("Errore nella rimozione della terapia concomitante.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}
