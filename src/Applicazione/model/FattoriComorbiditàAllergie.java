package application.model;


public class FattoriComorbiditàAllergie {

	private String cf;
	private String nome;
	private String modificato;
	
	public FattoriComorbiditàAllergie(String cf, String nome, String modificato) {
		this.cf = cf;
		this.nome = nome;
		this.modificato = modificato;
	}
	
	public String getCF() {
		return cf;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getModificato() {
		return modificato;
	}
}
