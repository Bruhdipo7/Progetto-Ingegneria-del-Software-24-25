package controller;

import java.io.IOException;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Patologia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PatologiaController {
	
	private Patologia patologia;
	
	@FXML private Label nomeLabel;
	@FXML private Label dataLabel;
	@FXML private Label indicazioniLabel;
	@FXML private Label modificatoLabel;
	
	@FXML
	private void initialize() {
		patologia = Sessione.getInstance().getPatologiaSelezionata();
		
		nomeLabel.setText(patologia.getNome());
		dataLabel.setText(patologia.getInizio().format(Amministratore.dateFormatter));
		indicazioniLabel.setText(patologia.getIndicazioni());
		
		Amministratore.diabetologi.stream()
			.filter(d -> d.getCf().equals(patologia.getModificato()))
			.findFirst()
			.ifPresent(d -> {
				modificatoLabel.setText(d.getNomeCognome() + " (" + d.getCf() + ")");
			});
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Sessione.getInstance().nullPatologiaSelezionata();
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}
