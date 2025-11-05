package application.model;

import java.time.LocalDate;

public class Utente {

	private String cf;
	private String pw;
	private String ruolo;
	private String nomeCognome;
	private LocalDate dataDiNascita;
	private String sesso;
	private String path;
	private String mail;
	private String diabetologoRif;
	
	public Utente(String cf, String pw, String ruolo, String nomeCognome, LocalDate dataDiNascita, String sesso, String path, String mail, String diabetologoRif) {
		this.cf = cf;
		this.pw = pw;
		this.ruolo = ruolo;
		this.nomeCognome = nomeCognome;
		this.dataDiNascita = dataDiNascita;
		this.sesso = sesso;
		this.path = path;
		this.mail = mail;
		this.diabetologoRif = diabetologoRif;
	}
	
	public boolean checkPw(String pw) {
		return this.pw.equals(pw);
	}
	
	public boolean checkRuolo(String ruolo) {
		return this.ruolo.equals(ruolo);
	}
	
	public String getCf() {
		return cf;
	}

	public String getPw() {
		return pw;
	}
	
	public String getRuolo() {
		return ruolo;
	}
	
	public String getNomeCognome() {
		return nomeCognome;
	}
	
	public LocalDate getDataDiNascita() {
		return dataDiNascita;
	}
	
	public String getSesso() {
		return sesso;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getDiabetologoRif() {
		return diabetologoRif;
	}
}
