package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import application.Amministratore;
import application.Database;
import application.Navigator;
import application.Sessione;
import application.model.Mail;
import application.model.Questionario;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ScriviMailController {

	private Utente u;
	private Utente p;
	private Questionario q;
	
	// FXML
	@FXML private TextField destinatarioField;
	@FXML private TextField oggettoField;
	@FXML private TextArea corpoArea;
	
	// VARIABILI
	private String cfDiabetologoRIF = null;
	private int id;
	
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		p = Sessione.getInstance().getPazienteSelezionato();
		q = Sessione.getInstance().getQuestionarioSelezionato();
		
		if(u.getRuolo().equals("paziente"))
			cfDiabetologoRIF = u.getDiabetologoRif();

		if(cfDiabetologoRIF != null) {
			Amministratore.diabetologi.stream()
				.filter(d -> d.getCf().equals(cfDiabetologoRIF))
				.findFirst()
				.ifPresentOrElse(d -> {
					destinatarioField.setText(d.getMail());
				}, null);
		}
		
		if(p != null) {
			destinatarioField.setText(p.getMail());
		}
		
	}
	
	@FXML
	private void handleMail(ActionEvent event) throws IOException {
		
		if(destinatarioField.getText().isBlank() || oggettoField.getText().isBlank() || corpoArea.getText().isBlank()) {
			Amministratore.msgController.showErrorWindow("Per scrivere una mail bisogna compilare tutti i campi.");
			return;
		}
		
		String query = "INSERT INTO mail (mittente, destinatario, oggetto, corpo, giorno, orario, letta) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = Database.getConnection(); 
	    		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

		    	stmt.setString(1, u.getMail());
		    	stmt.setString(2, destinatarioField.getText());
		    	stmt.setString(3, oggettoField.getText());
		    	stmt.setString(4, corpoArea.getText());
		    	stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
		    	stmt.setTime(6, java.sql.Time.valueOf(LocalTime.now()));
		    	stmt.setBoolean(7, false);
		    	        
		    	int rows = stmt.executeUpdate();
	
		    	if (rows > 0) {
		    		try (ResultSet rs = stmt.getGeneratedKeys()) {
		                if (rs.next()) {
		                    id = rs.getInt(1); // recupera l'id auto_increment
		                }
		    		}
		    		Amministratore.mail.add(new Mail(
		    				id,
		    				u.getMail(),
		    				destinatarioField.getText(),
		    				oggettoField.getText(),
		    				corpoArea.getText(),
		    				LocalDate.now(),
		    				LocalTime.now(),
		    				false
		    			));
		    		Amministratore.loadMailFromDatabase();
		    		Amministratore.msgController.showSuccessWindow("Mail inviata.");
		    		if(q != null) {
		    			handleQuestionario();
		    		}
		    	    switchToMailPage(event);
		    	} else {
		    		Amministratore.msgController.showErrorWindow("Errore nel invio della mail.");
		    	}

		} catch (SQLException e) {
			e.printStackTrace();
	    }
		
	}
	
	private void handleQuestionario() {
		String query = "UPDATE questionario SET controllato = ? WHERE cf = ? AND giornoCompilazione = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, true);
            stmt.setString(2, q.getCf());
            stmt.setDate(3, java.sql.Date.valueOf(q.getGiornoCompilazione()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                Amministratore.loadQuestionarioFromDatabase();
            }
        } catch (SQLException ev) {
            ev.printStackTrace();
        }
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMailPage(ActionEvent event) throws IOException {
		Sessione.getInstance().nullQuestionarioSelezionato();
		Sessione.getInstance().nullPazienteSelezionato();
		Navigator.getInstance().switchToMailPage(event);
	}
}
