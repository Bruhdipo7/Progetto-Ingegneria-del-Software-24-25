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
import application.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MailController {
	
	// --- SEZIONE VARIABILI LOCALI ---	
	private Utente u;
	private String nome_cognome;
	private int id;
	
	// --- SEZIONE PAGINE ---
	@FXML private VBox scriviPanel;
	
	// --- SEZIONE BOTTONI ---
	@FXML private Button bottoneIndietro;
	
	// --- SEZIONE TEXTFIELD ---
	@FXML private TextField searchMailBar;
	@FXML private TextField destinatarioField;
	@FXML private TextField oggettoField;
	
	// --- SEZIONE TEXTAREA ---
	@FXML private TextArea corpoArea;
	
	// --- SEZIONI LISTEVIEW ---
	@FXML private ListView<Mail> listaMail;
	ObservableList<Mail> listaMailAsObservable;
	ObservableList<Mail> listaMailInviateAsObservable;
	
	@FXML private Label mailNonLette;
	
	@FXML public void initialize() throws IOException{
		u = Sessione.getInstance().getUtente();
		
		//Impostazione bottoneIndietro
		if ("diabetologo".equals(u.getRuolo())) {
    		bottoneIndietro.setOnAction(e -> {
                try {
                	Navigator.getInstance().switchToDiabetologoPage(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } else if ("paziente".equals(u.getRuolo())) {
        	bottoneIndietro.setOnAction(e -> {
                try {
                	Navigator.getInstance().switchToPazientePage(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
	
		// Mail ricevute
		listaMailAsObservable = FXCollections.observableArrayList(
		    Amministratore.mail.stream()
		        .filter(m -> u.getMail().equals(m.getDestinatario()))
		        .toList()
		);
		
		// Impostazione nelle ListView
		listaMail.setItems(listaMailAsObservable);
		
		// CELL FACTORY: mail non lette in grassetto + sfondo diverso
		listaMail.setCellFactory(_ -> new ListCell<Mail>() {
		    protected void updateItem(Mail mail, boolean empty) {
		        super.updateItem(mail, empty);
		        
		        if (empty || mail == null) {
		            setText(null);
		            setStyle("");
		        } else {
		        	nome_cognome = Amministratore.utenti.stream()
		        		.filter(p -> p.getMail().equals(mail.getMittente()))
		        		.map(Utente::getNomeCognome)
		        		.findFirst()
		        		.orElse(null);
		        	
		            setText(nome_cognome + "\nOggetto: " + mail.getOggetto());

		            if (!mail.getLetta()) {
		                // NON LETTA: grassetto + sfondo azzurrino
		                setStyle("-fx-font-weight: bold; -fx-background-color: #f0f8ff;");
		            } else {
		                setStyle("");
		            }
		        }
		    }
		});
		
		mailNonLette.setText("Non lette: " + Amministratore.contatoreMailNonLette());
		
		// VEDI UNA SPECIFICA MAIL
		listaMail.setOnMouseClicked(e -> {
			Mail selectedMail = listaMail.getSelectionModel().getSelectedItem();
			if(selectedMail != null) {
				try {
					Sessione.getInstance().setMailSelezionata(selectedMail);
					Navigator.getInstance().switchToVediMail(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
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
		    		hideCompose();
		    	} else {
		    		Amministratore.msgController.showErrorWindow("Errore nel invio della mail.");
		    	}

		} catch (SQLException e) {
			e.printStackTrace();
	    }
		
	}
	
	@FXML
	private void showMailRicevute(ActionEvent e) throws IOException {
		//Quando viene schiacciato il bottone ricevute
		//rimane sulle mail ricevute
		listaMail.setItems(listaMailAsObservable);
	}

	@FXML
	private void showMailInviate(ActionEvent e) throws IOException {
		//Quando schiaccia il bottone inviate
		//passa alle mail inviate
		// Mail inviate
		listaMailInviateAsObservable = FXCollections.observableArrayList(
		    Amministratore.mail.stream()
		        .filter(m -> u.getMail().equals(m.getMittente()))
		        .toList()
		);
		
		listaMail.setItems(listaMailInviateAsObservable);
	}
	
	@FXML
    private void showCompose() {
        scriviPanel.setVisible(true);
        scriviPanel.setManaged(true);
    }

    @FXML
    private void hideCompose() {
        scriviPanel.setVisible(false);
        scriviPanel.setManaged(false);
        destinatarioField.clear();
        oggettoField.clear();
        corpoArea.clear();
    }
	
	@FXML
	private void switchToScriviPage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToScriviPage(event);
	}
}