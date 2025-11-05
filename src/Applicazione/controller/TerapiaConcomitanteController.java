package controller;

import java.io.IOException;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.TerapiaConcomitante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TerapiaConcomitanteController {
	
	private TerapiaConcomitante tc;
	
	@FXML private Label nomeLabel;
	@FXML private Label dataInizioLabel;
	@FXML private Label dataFineLabel;
	@FXML private Label modificatoLabel;
	
	@FXML
	private void initialize() {
		tc = Sessione.getInstance().getTerapiaConcomitanteSelezionata();
		
		nomeLabel.setText(tc.getNome());
		dataInizioLabel.setText(tc.getDataInizio().format(Amministratore.dateFormatter));
		dataFineLabel.setText(tc.getDataFine().format(Amministratore.dateFormatter));
		
		Amministratore.diabetologi.stream()
			.filter(d -> d.getCf().equals(tc.getModificato()))
			.findFirst()
			.ifPresent(d -> {
				modificatoLabel.setText(d.getNomeCognome() + " (" + d.getCf() + ")");
			});
	}
	
	// NAVIGAZIONE
	@FXML
	private void switchToMostraDatiPaziente(ActionEvent event) throws IOException {
		Sessione.getInstance().nullTerapiaConcomitanteSelezionata();
		Navigator.getInstance().switchToMostraDatiPaziente(event);
	}
}
