package application.model;

import java.time.LocalDate;

public class Glicemia {

	String cf;
	int valore;
	LocalDate giorno;
	String orario;
	String indicazioni;
	
	public Glicemia(String cf, int valore, LocalDate giorno, String orario, String indicazioni) {
		this.cf = cf;
		this.valore = valore;
		this.giorno = giorno;
		this.orario = orario;
		this.indicazioni = indicazioni;
	}
	
	public String getCf() {
		return cf;
	}
	
	public int getValore() {
		return valore;
	}
	
	public LocalDate getGiorno() {
		return giorno;
	}
	
	public String getOrario() {
		return orario;
	}
	
	public String getIndicazioni() {
		return indicazioni;
	}
}
