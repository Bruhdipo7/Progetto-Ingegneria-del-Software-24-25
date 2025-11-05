package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import application.Amministratore;
import application.Database;
import application.Navigator;
import application.Sessione;
import application.model.Mail;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VediMailController {
	
	private Utente u;
	private Mail m;
	
	// FXML
	@FXML private Label mittenteLabel;
	@FXML private Label destinatarioLabel;
	@FXML private Label oggettoLabel;
	@FXML private Label corpoLabel;
	@FXML private Label giornoOraLabel;
	
	@FXML
	private void initialize() throws IOException {
		u = Sessione.getInstance().getUtente();
		m = Sessione.getInstance().getMailSelezionata();
		
		if(m.getMittente().equals(u.getMail())) {
			mittenteLabel.setText(u.getMail());
			destinatarioLabel.setText(m.getDestinatario());
		} else if(m.getDestinatario().equals(u.getMail())) {
			mittenteLabel.setText(m.getMittente());
			destinatarioLabel.setText(u.getMail());
		}
		oggettoLabel.setText(m.getOggetto());
		corpoLabel.setText(m.getCorpo());
		giornoOraLabel.setText(m.getGiorno() + ", " + m.getOrario());
		
		// segna come letta la mail corrispondente
		if(!m.getLetta() && m.getDestinatario().equals(u.getMail())) {
			String query = "UPDATE mail SET letta = ? WHERE mittente = ? AND orario = ?";
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setBoolean(1, true);
	            stmt.setString(2, m.getMittente());
	            stmt.setTime(3, Time.valueOf(m.getOrario()));

	            int rows = stmt.executeUpdate();
	            if (rows > 0) {
	                Amministratore.loadMailFromDatabase();
	            }
	        } catch (SQLException ev) {
	            ev.printStackTrace();
	        }
		}
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMailPage(ActionEvent event) throws IOException {
		Sessione.getInstance().nullMailSelezionata();
		Navigator.getInstance().switchToMailPage(event);
	}
}
