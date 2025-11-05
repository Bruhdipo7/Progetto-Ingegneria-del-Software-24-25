package application;

import application.model.Mail;
import application.model.Patologia;
import application.model.Questionario;
import application.model.Terapia;
import application.model.TerapiaConcomitante;
import application.model.Utente;

public class Sessione {
	private static Sessione instance;
	
	private Utente utenteLoggato;
	private Utente pazienteSelezionato;
	private Terapia terapiaSelezionata;
	private TerapiaConcomitante terapiaConcomitanteSelezionata;
	private Patologia patologiaSelezionata;
	private Mail mailSelezionata;
	private Questionario questSelezionato;
	
	// COSTRUTTORE PRIVATO
	private Sessione() {}
	
	public static Sessione getInstance() {
		if(instance == null) {
			instance = new Sessione();
		}
		return instance;
	}
	
	//------------------------------------
	public void setUtente(Utente utente) {
		this.utenteLoggato = utente;
	}
	
	public Utente getUtente() {
		return utenteLoggato;
	}
	
	public void logout() {
		utenteLoggato= null;
		
		// NULL ANCHE IL RESTO PER PULIZIA SICURA
		nullPazienteSelezionato();
		nullTerapiaSelezionata();
		nullTerapiaConcomitanteSelezionata();
		nullPatologiaSelezionata();
		nullMailSelezionata();
		nullQuestionarioSelezionato();
	}
	//---------------------------------------------------
	public void setPazienteSelezionato(Utente paziente) {
		this.pazienteSelezionato = paziente;
	}
	
	public Utente getPazienteSelezionato() {
		return pazienteSelezionato;
	}
	
	public void nullPazienteSelezionato() {
		pazienteSelezionato= null;
	}
	//--------------------------------------------------
	public void setTerapiaSelezionata(Terapia terapia) {
		this.terapiaSelezionata = terapia;
	}
	
	public Terapia getTerapiaSelezionata() {
		return terapiaSelezionata;
	}
	
	public void nullTerapiaSelezionata() {
		terapiaSelezionata= null;
	}
	//--------------------------------------------------------------------------------------
	public void setTerapiaConcomitanteSelezionata(TerapiaConcomitante terapiaConcomitante) {
		this.terapiaConcomitanteSelezionata = terapiaConcomitante;
	}
	
	public TerapiaConcomitante getTerapiaConcomitanteSelezionata() {
		return terapiaConcomitanteSelezionata;
	}
	
	public void nullTerapiaConcomitanteSelezionata() {
		terapiaConcomitanteSelezionata= null;
	}
	//--------------------------------------------------------
	public void setPatologiaSelezionata(Patologia patologia) {
		this.patologiaSelezionata = patologia;
	}
	
	public Patologia getPatologiaSelezionata() {
		return patologiaSelezionata;
	}
	
	public void nullPatologiaSelezionata() {
		patologiaSelezionata= null;
	}
	//-----------------------------------------
	public void setMailSelezionata(Mail mail) {
		this.mailSelezionata = mail;
	}
	
	public Mail getMailSelezionata() {
		return mailSelezionata;
	}
	
	public void nullMailSelezionata() {
		mailSelezionata= null;
	}
	//----------------------------------------------------------
	public void setQuestionarioSelezionato(Questionario quest) {
		this.questSelezionato = quest;
	}
	
	public Questionario getQuestionarioSelezionato() {
		return questSelezionato;
	}
	
	public void nullQuestionarioSelezionato() {
		questSelezionato = null;
	}
}
