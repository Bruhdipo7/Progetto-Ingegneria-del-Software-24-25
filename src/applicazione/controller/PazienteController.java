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
import application.model.Glicemia;
import application.model.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PazienteController {

	private Utente u;
	
	// FXML
	@FXML private LineChart<String, Number> graficoGlicemia;
	@FXML private TextField valoreField;
	@FXML private TextField oraField;
	@FXML private TextField minutiField;
	@FXML private ComboBox<String> indicazioniBox;
	@FXML private Label welcomeLabel;
	@FXML private Label statoQuestionarioOdierno;
	@FXML private Button mailButton;
	@FXML private Button questButton;

	// VARIABILI
	int valore;
	LocalDate giorno;
	String ora;
	String minuti;
	String indicazioni;
	boolean compilato = false;
	boolean terapiaInCorso = false;

	
	@FXML
	private void initialize() throws IOException {
		u = Sessione.getInstance().getUtente();
		
		welcomeLabel.setText("Ciao, " + u.getNomeCognome());
		
		graficoGlicemia.setFocusTraversable(true);
		
		Amministratore.questionari.stream()
			.filter(q -> q.getCf().equals(u.getCf())
						&& q.getGiornoCompilazione().equals(LocalDate.now()))
			.findFirst()
			.ifPresent(_ -> {
				statoQuestionarioOdierno.setText("Questionario odierno compilato!");
				compilato = true;
				questButton.setDisable(true);
			});
		
		Amministratore.terapie.stream()
			.filter(t -> t.getCf().equals(u.getCf())
						&& ((t.getDataInizio().isBefore(LocalDate.now()) || t.getDataInizio().isEqual(LocalDate.now())) 
						&& ((t.getDataFine().isAfter(LocalDate.now())) || t.getDataFine().isEqual(LocalDate.now()))))
			.findAny()
			.ifPresent(_ -> {
				if(compilato == false) {
					statoQuestionarioOdierno.setText("Questionario odierno da compilare!");
				}
				terapiaInCorso = true;
			});
		
		if(terapiaInCorso == false) {
			questButton.setDisable(true);
			statoQuestionarioOdierno.setText("Nessun questionario da compilare!");
		}
			
		mailButton.setText(Amministratore.contatoreMailNonLette() > 0 ? Amministratore.contatoreMailNonLette() + " Mail" : "Mail");
	    mailButton.setStyle(Amministratore.contatoreMailNonLette() > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: black;");
	    
	    visualizzaGrafico();
	}
	
	private void visualizzaGrafico() {
	    XYChart.Series<String, Number> serie = new XYChart.Series<>();
	    serie.setName("Glicemia giornaliera");

	    for(Glicemia glicemia : Amministratore.glicemia) {
	    	if(glicemia.getCf().equals(u.getCf()) && glicemia.getGiorno().isEqual(LocalDate.now())) {
	    		
	    		final int valore = glicemia.getValore();
	            final String orario = glicemia.getOrario();
	            final String indicazioni = glicemia.getIndicazioni();
	            //final LocalDate giorno = glicemia.getGiorno();
	    		
	    		XYChart.Data<String, Number> punto = new XYChart.Data<>(orario, valore);
	    		
	    		punto.nodeProperty().addListener((_, _, newNode) -> { // underscore al posto di obs e oldNode in quanto non usati
	                if (newNode != null) {
	                    if(indicazioni.equals("Pre pasto")) {
	                    	if(valore < 80 || valore > 130)
		                        newNode.setStyle("-fx-background-color: red;");
		                    else
		                        newNode.setStyle("-fx-background-color: green;");
	                    } else if(indicazioni.equals("Post pasto")) {
	                    	if(valore > 180)
	                    		newNode.setStyle("-fx-background-color: red;");
	                    	else
	                    		newNode.setStyle("-fx-background-color: green;");
	                    }
	                }
	            });
	    		
	    		serie.getData().add(punto);
	    	}
	    }
	    
	    graficoGlicemia.getData().clear(); //cancella la precedente
        graficoGlicemia.getData().add(serie);

	    indicazioniBox.getItems().clear();
	    indicazioniBox.getItems().addAll("Pre pasto", "Post pasto");
	}
	
	@FXML
	private void handleGlicemia(ActionEvent event) throws IOException {
	    String valoreText = valoreField.getText().trim();
	    ora = oraField.getText().trim();
	    minuti = minutiField.getText().trim();
	    indicazioni = indicazioniBox.getValue();

	    if (valoreText.isEmpty() || ora.isEmpty() || minuti.isEmpty() || indicazioni == null) {
	    	Amministratore.msgController.showErrorWindow("Compilare tutti i campi.");
	        return;
	    }

	    int oraInt, minutiInt;
	    try {
	        oraInt = Integer.parseInt(ora);
	        minutiInt = Integer.parseInt(minuti);
	        valore = Integer.parseInt(valoreText);
	    } catch (NumberFormatException e) {
	    	Amministratore.msgController.showErrorWindow("Valore, ora e minuti devono essere numeri interi.");
	        return;
	    }

	    if (oraInt < 0 || oraInt > 23) {
	    	Amministratore.msgController.showErrorWindow("Ora non valida.");
	        return;
	    }
	    if (ora.length() == 1) ora = "0" + ora;

	    if (minutiInt < 0 || minutiInt > 59) {
	    	Amministratore.msgController.showErrorWindow("Minuti non validi.");
	        return;
	    }
	    if (minuti.length() == 1) minuti = "0" + minuti;

	    String orario = ora + ":" + minuti;
	    giorno = LocalDate.now();

	    String query = "INSERT INTO glicemia (CF, valore, giorno, orario, indicazioni) VALUES (?, ?, ?, ?, ?)";
	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, u.getCf());
	        stmt.setInt(2, valore);
	        stmt.setDate(3, java.sql.Date.valueOf(giorno));
	        stmt.setString(4, orario);
	        stmt.setString(5, indicazioni);

	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
        		Amministratore.glicemia.add(new Glicemia(u.getCf(), valore, giorno, orario, indicazioni));
        		Amministratore.msgController.showSuccessWindow("Valore inserito.");
	            valoreField.clear();
	            oraField.clear();
	            minutiField.clear();
	            
	            //SISTEMARE IL RESET DELLA COMBO BOX
	            indicazioniBox.getSelectionModel().clearSelection();
	        } else {
	        	Amministratore.msgController.showErrorWindow("Errore nell'inserimento del valore.");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    // Recupero la serie già presente nel grafico
	    if (!graficoGlicemia.getData().isEmpty()) {
	        XYChart.Series<String, Number> serie = graficoGlicemia.getData().get(0);

	        XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(orario, valore);

	        dataPoint.nodeProperty().addListener((_, _, newNode) -> { // underscore al posto di obs e oldNode in quanto non usati
	            if (newNode != null) {
	                if(indicazioni.equals("Pre pasto")) {
                    	if(valore < 80 || valore > 130)
	                        newNode.setStyle("-fx-background-color: red;");
	                    else
	                        newNode.setStyle("-fx-background-color: green;");
                    } else if(indicazioni.equals("Post pasto")) {
                    	if(valore > 180)
                    		newNode.setStyle("-fx-background-color: red;");
                    	else
                    		newNode.setStyle("-fx-background-color: green;");
                    }
	            }
	        });

	        serie.getData().add(dataPoint);
	    }
	    
	    Amministratore.loadGlicemiaFromDatabase();
		try {
			Navigator.getInstance().switchToPazientePage(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	private void switchToQuestionarioPage(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToQuestionarioPage(event);
	}
	
	@FXML
	private void switchToProfiloPaziente(ActionEvent event) throws IOException {
		Navigator.getInstance().switchToProfiloPaziente(event);
	}
}
