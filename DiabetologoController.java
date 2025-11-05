package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;

import application.Amministratore;
import application.Navigator;
import application.Sessione;
import application.model.Glicemia;
import application.model.Questionario;
import application.model.Utente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class DiabetologoController {
		
	private Utente u;
	
	// FXML
	@FXML private Label welcomeLabel;
	@FXML private ListView<Utente> listaNomiPazienti;
	@FXML private ListView<Questionario> listaNotificheQuestionario;
	@FXML private ListView<Glicemia> listaGlicemieSballate;
	@FXML private ListView<Questionario> listaQuestNonConformi;
	@FXML private Button mailButton;
	
	// VARIABILI
	private String cf;
	
	@FXML
	private void initialize() {
		u = Sessione.getInstance().getUtente();
		
		welcomeLabel.setText("Ciao, " + u.getNomeCognome());
		welcomeLabel.setFocusTraversable(true);
		
		mailButton.setText(Amministratore.contatoreMailNonLette() > 0 ? Amministratore.contatoreMailNonLette() + " Mail" : "Mail");
		mailButton.setStyle(Amministratore.contatoreMailNonLette() > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
		
		setupListaPazienti();
		setupListaGlicemieSballate();
		setupListaQuestionariNonConformi();

	}
	
	private void setupListaPazienti(){
		ObservableList<Utente> listaNomiPazientiAsObservable = FXCollections.observableArrayList(
				Amministratore.pazienti.stream()
			        .sorted(Comparator.comparing(Utente::getNomeCognome)) // ordine alfabetico
			        .toList()
			);
		listaNomiPazienti.setItems(listaNomiPazientiAsObservable);
		
		listaNomiPazienti.setCellFactory(_ -> new ListCell<Utente>() {
		    protected void updateItem(Utente paziente, boolean empty) {
		        super.updateItem(paziente, empty);
		        
		        if (empty || paziente == null) {
		            setText(null);
		            setStyle("");
		        } else {
		        	Amministratore.questionari.stream()
		        		.filter(q -> q.getCf().equals(paziente.getCf()))
		        		.findFirst()
		        		.ifPresentOrElse(
		        			q -> {
		        				if(LocalDate.now().isAfter(q.getGiornoCompilazione().plusDays(3))) {
		        					setText(paziente.getNomeCognome() + " (" + paziente.getCf() + ") non compila il questionario dal " 
		        							+ q.getGiornoCompilazione());
		        					setStyle("-fx-font-weight: bold; -fx-background-color: #f0f8ff;");
		        				} else {
		        					setText(paziente.getNomeCognome() + " (" + paziente.getCf() + ")");
		        				}
		        			},
		        			    
		        			() -> setText(paziente.getNomeCognome() + " (" + paziente.getCf() + ")")
		        		);
		        }
		    }
		});
		
		listaNomiPazienti.setOnMouseClicked(e -> {
			Utente selectedPaziente = listaNomiPazienti.getSelectionModel().getSelectedItem();
			if(selectedPaziente != null) {
				String cf = selectedPaziente.getCf();
				Amministratore.pazienti.stream()
					.filter(paziente -> paziente.getCf().equals(cf))
					.findFirst()
					.ifPresent(paziente -> {
						Sessione.getInstance().setPazienteSelezionato(paziente);
					});
				
				try {
					Navigator.getInstance().switchToMostraDatiPaziente(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	private void setupListaGlicemieSballate() {
		ObservableList<Glicemia> listaGlicemieSballateAsObservable = FXCollections.observableArrayList(
			    Amministratore.glicemia.stream()
			        .filter(glicemia -> glicemia.getGiorno().equals(LocalDate.now()) || glicemia.getGiorno().plusDays(1).equals(LocalDate.now()))
			        .filter(glicemia -> {
			            String indicazioni = glicemia.getIndicazioni();
			            int val = glicemia.getValore();
			            // Rosso
			            if ((indicazioni.equals("Pre pasto") && (val < 60 || val > 150)) ||
			                (indicazioni.equals("Post pasto") && val > 200)) return true;
			            // Arancione
			            if ((indicazioni.equals("Pre pasto") && (val < 70 || val > 140)) ||
			                (indicazioni.equals("Post pasto") && val > 190)) return true;
			            // Giallo
			            if ((indicazioni.equals("Pre pasto") && (val < 80 || val > 130)) ||
			                (indicazioni.equals("Post pasto") && val > 180)) return true;
			            return false;
			        })
			        .toList()
			);
		listaGlicemieSballate.setItems(listaGlicemieSballateAsObservable);
		listaGlicemieSballate.setCellFactory(_ -> new ListCell<Glicemia>() {
		    protected void updateItem(Glicemia glicemia, boolean empty) {
		        super.updateItem(glicemia, empty);
		        if (empty || glicemia == null) {
		            setText(null);
		            setStyle("");
		            return;
		        }
		        
		        Amministratore.pazienti.stream()
		        	.filter(p -> p.getCf().equals(glicemia.getCf()))
		        	.findFirst()
		        	.ifPresent(p -> {
		        		setText(p.getNomeCognome() + " (" + p.getCf() + "): " + glicemia.getValore());
		        	});

		        String color;

		        if ((glicemia.getIndicazioni().equals("Pre pasto") && (glicemia.getValore() < 60 || glicemia.getValore() > 150)) ||
		            (glicemia.getIndicazioni().equals("Post pasto") && glicemia.getValore() > 200)) {
		            color = "#FF0000"; // rosso
		        } else if ((glicemia.getIndicazioni().equals("Pre pasto") && (glicemia.getValore() < 70 || glicemia.getValore() > 140)) ||
		                   (glicemia.getIndicazioni().equals("Post pasto") && glicemia.getValore() > 190)) {
		            color = "#FFA500"; // arancione
		        } else {
		            color = "#FFD700"; // giallo
		        }

		        setStyle("-fx-text-fill: " + color + ";");
		    }
		});
	}
	
	private void setupListaQuestionariNonConformi() {
		ObservableList<Questionario> listaQuestNonConformiAsObservable = FXCollections.observableArrayList(
	        Amministratore.questionari.stream()
	            .filter(q ->
	                Amministratore.terapie.stream().anyMatch(t ->
	                    t.getCf().equals(q.getCf())
	                    && !t.getDataInizio().isAfter(q.getGiornoCompilazione())
	                    && !t.getDataFine().isBefore(q.getGiornoCompilazione())
	                    && (
	                        !q.getNomeFarmaco().equals(t.getNomeFarmaco()) ||
	                        q.getDosiGiornaliere() != t.getDosiGiornaliere() ||
	                        q.getQuantità() != t.getQuantità()
	                    )
	                )
	            )
	            .filter(q -> Amministratore.pazienti.stream()
	            		.anyMatch(p -> p.getCf().equals(q.getCf()) && p.getDiabetologoRif().equals(u.getCf())))
	            .toList()
		);
	
		listaQuestNonConformi.setItems(listaQuestNonConformiAsObservable);
		
		listaQuestNonConformi.setCellFactory(_ -> new ListCell<Questionario>() {
		    protected void updateItem(Questionario quest, boolean empty) {
		        super.updateItem(quest, empty);
		        
		        if (empty || quest == null) {
		            setText(null);
		            setStyle("");
		        } else {
		            setText("Questionario del " + quest.getGiornoCompilazione() +
		                    "\nnon conforme alla terapia: " + quest.getNomeFarmaco() +
		                    "\ndi: " + quest.getCf());
		            
		            if (!quest.getControllato()) {
		                // NON CONTROLLATO: grassetto + sfondo azzurrino
		                setStyle("-fx-font-weight: bold; -fx-background-color: #f0f8ff;");
		            } else {
		                setStyle("");
		            }
		        }
		    }
		});
		
		listaQuestNonConformi.setOnMouseClicked(e -> {
			Questionario selectedQuestNonConformi = listaQuestNonConformi.getSelectionModel().getSelectedItem();
			if(selectedQuestNonConformi != null) {
				cf = selectedQuestNonConformi.getCf();
				Amministratore.pazienti.stream()
					.filter(paziente -> paziente.getCf().equals(cf))
					.findFirst()
					.ifPresent(paziente -> {
						Sessione.getInstance().setPazienteSelezionato(paziente);
					});
				Sessione.getInstance().setQuestionarioSelezionato(selectedQuestNonConformi);
				try {
					Navigator.getInstance().switchToScriviPage(e);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
		
	// NAVIGAZIONE
	@FXML
	private void switchToLogin(ActionEvent event) throws IOException {
		Sessione.getInstance().logout();
		Navigator.getInstance().switchToLogin(event);
	}
	
	@FXML
	private void switchToMailPage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToMailPage(event);
	}
	
	@FXML
	private void switchToProfiloDiabetologo(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToProfiloDiabetologo(event);
	}
}
