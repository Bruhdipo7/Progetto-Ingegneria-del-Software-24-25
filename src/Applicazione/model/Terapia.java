package application.model;

import java.time.LocalDate;

public class Terapia {

	private int id;
	private String cf;
	private String nomeFarmaco;
	private int dosiGiornaliere;
	private int quantità;
	private LocalDate dataInizio;
	private LocalDate dataFine;
	private String indicazioni;
	private String modificato;
	
	public Terapia(int id, String cf, String nomeFarmaco, int dosiGiornaliere, int quantità, LocalDate dataInizio, LocalDate dataFine, String indicazioni, String modificato) {
		this.id = id;
		this.cf = cf;
		this.nomeFarmaco = nomeFarmaco;
		this.dosiGiornaliere = dosiGiornaliere;
		this.quantità = quantità;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.indicazioni = indicazioni;
		this.modificato = modificato;
	}
	
	public int getId() {
		return id;
	}
	
	public String getCf() {
		return cf;
	}
	
	public String getNomeFarmaco() {
		return nomeFarmaco;
	}
	
	public int getDosiGiornaliere() {
		return dosiGiornaliere;
	}
	
	public int getQuantità() {
		return quantità;
	}
	
	public LocalDate getDataInizio() {
		return dataInizio;
	}
	
	public LocalDate getDataFine() {
		return dataFine;
	}
	
	public String getIndicazioni() {
		return indicazioni;
	}
	
	public String getModificato() {
		return modificato;
	}
}
