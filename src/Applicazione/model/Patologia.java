package application.model;

import java.time.LocalDate;

public class Patologia {

	private String cf;
	private String nome;
	private LocalDate inizio;
	private String indicazioni;
	private String modificato;

	public Patologia(String cf, String nome, LocalDate inizio, String indicazioni, String modificato) {
		this.cf = cf;
		this.nome = nome;
		this.inizio = inizio;
		this.indicazioni = indicazioni;
		this.modificato = modificato;
	}
	
	public String getCf() {
		return cf;
	}
	
	public String getNome() {
		return nome;
	}
	
	public LocalDate getInizio() {
		return inizio;
	}
	
	public String getIndicazioni() {
		return indicazioni;
	}
	
	public String getModificato() {
		return modificato;
	}
}
