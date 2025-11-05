package application.model;

import java.time.LocalDate;

public class TerapiaConcomitante {

	private String cf;
	private String nome;
	private LocalDate dataInizio;
	private LocalDate dataFine;
	private String modificato;
	
	public TerapiaConcomitante(String cf, String nome, LocalDate dataInizio, LocalDate dataFine, String modificato) {
		this.cf = cf;
		this.nome = nome;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.modificato = modificato;
	}
	
	public String getCf() {
		return cf;
	}
	
	public String getNome() {
		return nome;
	}
	
	public LocalDate getDataInizio() {
		return dataInizio;
	}
	
	public LocalDate getDataFine() {
		return dataFine;
	}
	
	public String getModificato() {
		return modificato;
	}
}
