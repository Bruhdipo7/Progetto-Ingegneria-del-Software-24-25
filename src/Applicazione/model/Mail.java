package application.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Mail {

	private int id;
	private String mittente;
	private String destinatario;
	private String oggetto;
	private String corpo;
	private LocalDate giorno;
	private LocalTime orario;
	private boolean letta = false;

	public Mail(int id, String mittente, String destinatario, String oggetto, String corpo, LocalDate giorno, LocalTime orario, boolean letta) {
		this.id = id;
		this.mittente = mittente;
		this.destinatario = destinatario;
		this.oggetto = oggetto;
		this.corpo = corpo;
		this.giorno = giorno;
		this.orario = orario;
		this.letta = letta;
	}
	
	public int getId() {
		return id;
	}
	
	public String getMittente() {
		return mittente;
	}
	
	public String getDestinatario() {
		return destinatario;
	}
	
	public String getOggetto() {
		return oggetto;
	}
	
	public String getCorpo() {
		return corpo;
	}
	
	public LocalDate getGiorno() {
		return giorno;
	}
	
	public LocalTime getOrario() {
		return orario;
	}
	
	public boolean getLetta() {
		return letta;
	}
}
