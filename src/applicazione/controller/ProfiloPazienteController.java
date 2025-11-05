package controller;

import java.io.IOException;
import java.time.LocalDate;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;

public class ProfiloPazienteController {

	private Utente u;
	
	// LABEL - PROFILO
	@FXML private Label labelProfilo;
	@FXML private Label nomeCognomeLabel;
	@FXML private Label dateOfBirthLabel;
	@FXML private Label sexLabel;
	@FXML private ImageView fotoProfiloImage;
	
	// LABEL - TERAPIA CORRENTE
	@FXML private Label nomeFarmacoLabel;
	@FXML private Label dosiGiornaliereLabel;
	@FXML private Label quantitàLabel;
	@FXML private Label dataInizioLabel;
	@FXML private Label dataFineLabel;
	@FXML private Label indicazioniLabel;
	@FXML private TitledPane terapiaCorrente;
		
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
		
		Amministratore.terapie.stream()
			.filter(t -> t.getCf().equals(u.getCf())
						&& !t.getDataInizio().isAfter(LocalDate.now())
						&& !t.getDataFine().isBefore(LocalDate.now()))
			.findFirst()
			.ifPresentOrElse(t -> {
				nomeFarmacoLabel.setText(t.getNomeFarmaco());
				dosiGiornaliereLabel.setText(String.valueOf(t.getDosiGiornaliere()));
				quantitàLabel.setText(String.valueOf(t.getQuantità()));
				dataInizioLabel.setText(t.getDataInizio().format(Amministratore.dateFormatter));
				dataFineLabel.setText(t.getDataFine().format(Amministratore.dateFormatter));
				
				if(t.getIndicazioni() != null && !t.getIndicazioni().isBlank())
					indicazioniLabel.setText(t.getIndicazioni());
				else
					indicazioniLabel.setText("Nessuna indicazione.");
			}, () -> {
				terapiaCorrente.setText("Nessuna terapia in corso");
				nomeFarmacoLabel.setText("-");
				dosiGiornaliereLabel.setText("-");
				quantitàLabel.setText("-");
				dataInizioLabel.setText("----/--/--");
				dataFineLabel.setText("----/--/--");
				indicazioniLabel.setText("-");
			});
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToPazientePage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToPazientePage(event);
	}
}
