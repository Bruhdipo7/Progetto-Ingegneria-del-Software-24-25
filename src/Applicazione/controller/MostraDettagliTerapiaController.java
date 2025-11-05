package controller;

import java.io.IOException;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Terapia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MostraDettagliTerapiaController {
	
	private Terapia t;
	
	// LABEL
	@FXML private Label nomeFarmacoLabel;
	@FXML private Label dosiGiornaliereLabel;
	@FXML private Label quantitàLabel;
	@FXML private Label dataInizioLabel;
	@FXML private Label dataFineLabel;
	@FXML private Label indicazioniLabel;
	@FXML private Label modificatoDaLabel;
	
	
	@FXML
	private void initialize() {
		t = Sessione.getInstance().getTerapiaSelezionata();
		
		nomeFarmacoLabel.setText(t.getNomeFarmaco());
		dosiGiornaliereLabel.setText(String.valueOf(t.getDosiGiornaliere()));
		quantitàLabel.setText(String.valueOf(t.getQuantità()));
		dataInizioLabel.setText(t.getDataInizio().format(Amministratore.dateFormatter));
		dataFineLabel.setText(t.getDataFine().format(Amministratore.dateFormatter));
		
		if(t.getIndicazioni() != null && !t.getIndicazioni().isBlank())
			indicazioniLabel.setText(t.getIndicazioni());
		else
			indicazioniLabel.setText("Nessuna indicazione.");
		
		modificatoDaLabel.setText(t.getModificato());
	}
	
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Sessione.getInstance().nullTerapiaSelezionata();
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
	
	@FXML
	private void switchToModificaTerapia(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToModificaTerapia(event);
	}
}