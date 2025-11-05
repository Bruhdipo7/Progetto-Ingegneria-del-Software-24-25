package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Navigator {
	
	// Singleton
    private static Navigator instance;

    private Stage stage; // riferimento alla finestra principale

    private Navigator() {}

    public static Navigator getInstance() {
        if (instance == null)
            instance = new Navigator();
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
	
	//---------------------------------------------------------------
	public void switchToLogin(ActionEvent event) throws IOException {
		loadScene("/fxml/Login.fxml");
	}

	//-------------------------------------------------------------------------
	public void switchToDiabetologoPage(ActionEvent event) throws IOException {
		loadScene("/fxml/DiabetologoPage.fxml");
	}
	
	public void switchToProfiloDiabetologo(ActionEvent event) throws IOException {
		loadScene("/fxml/ProfiloDiabetologo.fxml");
	}
			
	//----------------------------------------------------------------------
	public void switchToPazientePage(ActionEvent event) throws IOException {
		loadScene("/fxml/PazientePage.fxml");
	}
	
	public void switchToProfiloPaziente(ActionEvent event) throws IOException {
		loadScene("/fxml/ProfiloPaziente.fxml");
	}
	
	//------------------------------------------------------------------------------------
	public void switchToMostraDatiPaziente(Event event) throws IOException {
		loadScene("/fxml/MostraDatiPaziente.fxml");
	}
	
	public void switchToMostraPatologia(Event event) throws IOException {
		loadScene("/fxml/PatologiaPregressa.fxml");
	}
	
	public void switchToMostraTerapiaConcomitante(Event event) throws IOException {
		loadScene("/fxml/TerapiaConcomitante.fxml");
	}
	
	//------------------------------------------------------------------
	public void switchToMailPage(ActionEvent event) throws IOException {
		loadScene("/fxml/MailPage.fxml");
	}
	
	public void switchToScriviPage(Event event) throws IOException {
		loadScene("/fxml/ScriviPage.fxml");
	}
	
	public void switchToVediMail(Event event) throws IOException {
		loadScene("/fxml/VediMail.fxml");
	}
	
	//--------------------------------------------------------------------------
	public void switchToQuestionarioPage(ActionEvent event) throws IOException {
		loadScene("/fxml/QuestionarioPage.fxml");
	}
	
	//----------------------------------------------------------------------
	public void switchToNuovaTerapia(ActionEvent event) throws IOException {
		loadScene("/fxml/NuovaTerapia.fxml");
	}
	
	public void switchToMostraDettagliTerapia(Event event) throws IOException {
		loadScene("/fxml/MostraDettagliTerapia.fxml");
	}
	
	public void switchToModificaTerapia(ActionEvent event) throws IOException {
		loadScene("/fxml/ModificaTerapia.fxml");
	}
	
	//----------------------------------------------------------------------------
	public void switchToStoriaDatiPaziente(ActionEvent event) throws IOException {
		loadScene("/fxml/StoriaDatiPaziente.fxml");
	}
	
	// -----------------------------------------------------------------
    private void loadScene(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();
        setScene(root);
    }
	
	//-----------------------------------------------
	private void setScene(Parent root) throws IOException {
		if(stage == null) {
			Amministratore.msgController.showErrorWindow("Stage non impostato.");
		}
		
        stage.setScene(new Scene(root));
        stage.show();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
    }
}
