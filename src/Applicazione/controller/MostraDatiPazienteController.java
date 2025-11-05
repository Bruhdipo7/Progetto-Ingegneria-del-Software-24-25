package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Glicemia;
import application.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class MostraDatiPazienteController {

	private Utente p;
	
	//VARIABILI
	private LocalDate date;
	private LocalDate date2;
	private String nomeDiabetologo;
	
	// GRAFICO
	@FXML private LineChart<String, Number> grafico;
	XYChart.Series<String, Number> serie = new XYChart.Series<>();

	
	//LABEL
	@FXML private Label labelPaziente;
	@FXML private Label dataDiNascitaDato;
	@FXML private Label sessoDato;
	@FXML private Label mailDato;
	@FXML private Label medicoRifLabel;
	@FXML private ComboBox<String> sceltaVisualizza;
	@FXML private DatePicker dataVisualizza;
	
	//LISTE
	@FXML public ListView<String> listaTerapiePaziente;
	ObservableList<String> listaTerapiePazienteAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaFattori;
	ObservableList<String> listaFattoriAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaComorbidità;
	ObservableList<String> listaComorbiditàAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaAllergie;
	ObservableList<String> listaAllergieAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaPatologie;
	ObservableList<String> listaPatologieAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaTerapieConcomitanti;
	ObservableList<String> listaTerapieConcomitantiAsObservable = FXCollections.observableArrayList();
	
	@FXML public ListView<String> listaQuestionari;
	ObservableList<String> listaQuestionariAsObservable = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		p = Sessione.getInstance().getPazienteSelezionato();
		
		labelPaziente.setText("Profilo clinico di " + p.getNomeCognome());
		dataDiNascitaDato.setText(p.getDataDiNascita().format(Amministratore.dateFormatter));
		sessoDato.setText(p.getSesso());
		mailDato.setText(p.getMail());
		
		Amministratore.diabetologi.stream()
			.filter(d -> d.getCf().equals(p.getDiabetologoRif()))
			.findFirst()
			.ifPresent(d -> {
				medicoRifLabel.setText("Diabetologo di riferimento: " + d.getNomeCognome() + " (" + d.getCf() + ")");
			});
			
		sceltaVisualizza.getItems().addAll("Settimana", "Mese");
		
		try {
			visualizzaDati();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void visualizzaDati() throws IOException {
		listaTerapiePazienteAsObservable.addAll(
			Amministratore.terapie.stream()
				.filter(terapia -> terapia.getCf().equals(p.getCf()))
				.map(terapia -> terapia.getNomeFarmaco() + " (" + terapia.getDataInizio() + ")")
				.toList()
		);
		listaTerapiePaziente.setItems(listaTerapiePazienteAsObservable);
		
		// ENTRA IN UNA SPECIFICA TERAPIA
		listaTerapiePaziente.setOnMouseClicked(e -> {
			String selectedTerapia = listaTerapiePaziente.getSelectionModel().getSelectedItem();
			if(selectedTerapia != null) {
				Amministratore.terapie.stream()
					.filter(terapia -> (terapia.getNomeFarmaco() + " (" + terapia.getDataInizio() + ")").equals(selectedTerapia))
					.findAny()
					.ifPresent(terapia -> {
						Sessione.getInstance().setTerapiaSelezionata(terapia);
					});
				
				try {
					Navigator.getInstance().switchToMostraDettagliTerapia(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	
		// FATTORI DI RISCHIO
		listaFattoriAsObservable.addAll(
			Amministratore.fattoriDiRischio.stream()
				.filter(fattore -> fattore.getCF().equals(p.getCf()))
				.map(fattore -> {
		            nomeDiabetologo = Amministratore.diabetologi.stream()
		                .filter(d -> d.getCf().equals(fattore.getModificato()))
		                .map(Utente::getNomeCognome)
		                .findFirst()
		                .orElse("Sconosciuto");
		            
		            return fattore.getNome() + " (Aggiunto da: " + nomeDiabetologo + ")";
		        })
		        .toList()
		);
		listaFattori.setItems(listaFattoriAsObservable);
		
		// COMORBIDITÀ
		listaComorbiditàAsObservable.addAll(
			Amministratore.comorbidità.stream()
			.filter(c -> c.getCF().equals(p.getCf()))
				.map(c -> {
		            nomeDiabetologo = Amministratore.diabetologi.stream()
		                .filter(d -> d.getCf().equals(c.getModificato()))
		                .map(Utente::getNomeCognome)
		                .findFirst()
		                .orElse("Sconosciuto");
		            
		            return c.getNome() + " (Aggiunto da: " + nomeDiabetologo + ")";
		        })
		        .toList()
		);
		listaComorbidità.setItems(listaComorbiditàAsObservable);
		
		// ALLERGIE
		listaAllergieAsObservable.addAll(
			Amministratore.allergie.stream()
				.filter(a -> a.getCF().equals(p.getCf()))
				.map(a -> {
		            nomeDiabetologo = Amministratore.diabetologi.stream()
		                .filter(d -> d.getCf().equals(a.getModificato()))
		                .map(Utente::getNomeCognome)
		                .findFirst()
		                .orElse("Sconosciuto");
		            
		            return a.getNome() + " (Aggiunto da: " + nomeDiabetologo + ")";
		        })
		        .toList()
		);
		listaAllergie.setItems(listaAllergieAsObservable);
		
		// PATOLOGIE
		listaPatologieAsObservable.addAll(
			Amministratore.patologie.stream()
				.filter(patologia -> patologia.getCf().equals(p.getCf()))
				.map(patologia -> patologia.getNome())
				.toList()
		);
		listaPatologie.setItems(listaPatologieAsObservable);
		
		// ENTRA IN UNA SPECIFICA PATOLOGIA
		listaPatologie.setOnMouseClicked(e -> {
			String selectedPatologia = listaPatologie.getSelectionModel().getSelectedItem();
			if(selectedPatologia != null) {
				Amministratore.patologie.stream()
					.filter(patologia -> patologia.getNome().equals(selectedPatologia))
					.findFirst()
					.ifPresent(patologia -> {
						Sessione.getInstance().setPatologiaSelezionata(patologia);
					});
				
				try {
					Navigator.getInstance().switchToMostraPatologia(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		// TERAPIE CONCOMITANTI
		listaTerapieConcomitantiAsObservable.addAll(
			Amministratore.terapieConcomitanti.stream()
				.filter(tc -> tc.getCf().equals(p.getCf()))
				.map(tc -> tc.getNome() + " (" + tc.getDataInizio() + ")")
				.toList()
		);
		listaTerapieConcomitanti.setItems(listaTerapieConcomitantiAsObservable);
		
		// ENTRA IN UNA SPECIFICA TERAPIA CONCOMITANTE
		listaTerapieConcomitanti.setOnMouseClicked(e -> {
			String selectedTerapiaConcomitante = listaTerapieConcomitanti.getSelectionModel().getSelectedItem();
			if(selectedTerapiaConcomitante != null) {
				Amministratore.terapieConcomitanti.stream()
					.filter(tc -> (tc.getNome() + " (" + tc.getDataInizio() + ")").equals(selectedTerapiaConcomitante))
					.findAny()
					.ifPresent(tc -> {
						Sessione.getInstance().setTerapiaConcomitanteSelezionata(tc);
					});
				
				try {
					Navigator.getInstance().switchToMostraTerapiaConcomitante(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		// LISTA QUESTIONARI
		listaQuestionariAsObservable.addAll(
			Amministratore.questionari.stream()
				.filter(quest -> quest.getCf().equals(p.getCf()))
				.map(quest -> quest.getNomeFarmaco() + " (" + quest.getGiornoCompilazione() + ")")
				.toList()
			);
		listaQuestionari.setItems(listaQuestionariAsObservable);
		
		// ENTRA IN UNO SPECIFICO QUESTIONARIO
		listaQuestionari.setOnMouseClicked(_ -> {
			String selectedQuestionario = listaQuestionari.getSelectionModel().getSelectedItem();
			if(selectedQuestionario != null) {
				Amministratore.questionari.stream()
					.filter(quest -> (quest.getNomeFarmaco() + " (" + quest.getGiornoCompilazione() + ")").equals(selectedQuestionario))
					.findAny()
					.ifPresent(quest -> {
						Sessione.getInstance().setQuestionarioSelezionato(quest);
					});
				
				System.out.println("Saresti entrato nel questionario selezionato.");
			}
		});
	}
	
	@FXML
	private void handleScelta() throws IOException {
		String scelta = sceltaVisualizza.getValue();
		date = dataVisualizza.getValue();
		if(date == null || scelta == null) {
			Amministratore.msgController.showErrorWindow("Scegli data e periodo.");
			return;
		} else if(date.isAfter(LocalDate.now())) {
			Amministratore.msgController.showErrorWindow("Scegliere una data antecedente a:\n" + LocalDate.now());
			return;
		}
		if("Settimana".equals(scelta)) {
			date2 = date.plusDays(7);
			if(date2.isAfter(LocalDate.now()))
				date2 = LocalDate.now();
		} else if("Mese".equals(scelta)) {
			date2 = date.plusMonths(1);
			if(date2.isAfter(LocalDate.now()))
				date2 = LocalDate.now();
		}
			
		grafico.getData().clear(); // svuota il grafico
		serie.getData().clear();   // svuota la serie
		
		serie.getData().addAll(
			Amministratore.glicemia.stream()
			.filter(g -> g.getCf().equals(p.getCf())
						&& g.getGiorno().isAfter(date.minusDays(1))
						&& g.getGiorno().isBefore(date2.plusDays(1)))
			.sorted(Comparator.comparing(Glicemia::getGiorno))
			.map(g -> new XYChart.Data<String, Number>(g.getGiorno().toString(), g.getValore()))
			.toList()
		);
		
        grafico.getData().add(serie);
			
	}
	
	@FXML
	private void switchToDiabetologoPage(ActionEvent event) throws IOException {
		Sessione.getInstance().nullPazienteSelezionato();
		Navigator.getInstance().switchToDiabetologoPage(event);
	}
	
	@FXML
	private void switchToNuovaTerapia(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToNuovaTerapia(event);
	}
	
	@FXML
	private void switchToStoriaDatiPaziente(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToStoriaDatiPaziente(event);
	}
}
