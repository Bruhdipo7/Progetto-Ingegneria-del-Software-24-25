package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import application.model.FattoriComorbiditàAllergie;
import application.model.Glicemia;
import application.model.Mail;
import application.model.Patologia;
import application.model.Questionario;
import application.model.Terapia;
import application.model.TerapiaConcomitante;
import application.model.Utente;
import controller.MessageController;

public class Amministratore {
	
	public static MessageController msgController = new MessageController();
	public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	// LISTE
	public static List<Utente> utenti = new ArrayList<>();
	public static List<Utente> pazienti = new ArrayList<>();
	public static List<Utente> diabetologi = new ArrayList<>();
	public static List<Terapia> terapie = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> fattoriDiRischio = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> comorbidità = new ArrayList<>();
	public static List<FattoriComorbiditàAllergie> allergie = new ArrayList<>();
	public static List<Patologia> patologie = new ArrayList<>();
	public static List<TerapiaConcomitante> terapieConcomitanti = new ArrayList<>();
	public static List<Glicemia> glicemia = new ArrayList<>();
	public static List<Mail> mail = new ArrayList<>();
	public static List<Questionario> questionari = new ArrayList<>();
	
	// CARICA UTENTI DAL DATABASE
	public static void loadUtentiFromDatabase() {
	    utenti.clear();

	    String query = "SELECT * FROM utenti";
	    try (Connection conn = Database.getConnection();
	    		PreparedStatement stmt = conn.prepareStatement(query);
	    		ResultSet rs = stmt.executeQuery()) {
	    	
            while (rs.next()) {
            	utenti.add(new Utente(
                        rs.getString("CF"),
                        rs.getString("pw"),
                        rs.getString("ruolo"),
                        rs.getString("nomeCognome"),
                        rs.getDate("dataDiNascita").toLocalDate(),
                        rs.getString("sesso"),
                        rs.getString("foto"),
                        rs.getString("mail"),
                        rs.getString("diabetologo_rif")
                    ));
            }
            
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	// CARICA TERAPIE DAL DATABASE
	public static void loadTerapieFromDatabase() {
		terapie.clear();

		String query = "SELECT * FROM terapie ORDER BY dataInizio DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				terapie.add(new Terapia(
						rs.getInt("id"),
						rs.getString("cf"),
						rs.getString("nomeFarmaco"),
						rs.getInt("dosiGiornaliere"),
						rs.getInt("quantità"),
						rs.getDate("dataInizio").toLocalDate(),
						rs.getDate("dataFine").toLocalDate(),
						rs.getString("indicazioni"),
						rs.getString("modificato")
					));
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CARICA STORIA DATI DAL DATABASE
	public static void loadFattoriComorbiditàAllergieFromDatabase() {
		fattoriDiRischio.clear();
		comorbidità.clear();
		
		String query = "SELECT * FROM fattoricomorbiditàallergie ORDER BY nome";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				if(rs.getString("tipo").equals("Fattore Di Rischio")) {
					fattoriDiRischio.add(new FattoriComorbiditàAllergie(
							rs.getString("CF"),
							rs.getString("nome"),
							rs.getString("modificato")));
				} 
				else if(rs.getString("tipo").equals("Comorbidità")) {
					comorbidità.add(new FattoriComorbiditàAllergie(
							rs.getString("CF"),
							rs.getString("nome"),
							rs.getString("modificato")));
				}
				else if(rs.getString("tipo").equals("Allergia")) {
					allergie.add(new FattoriComorbiditàAllergie(
							rs.getString("CF"),
							rs.getString("nome"),
							rs.getString("modificato")));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CARICA GLICEMIA DAL DATABASE
	public static void loadGlicemiaFromDatabase() {
		glicemia.clear();
		
		String query = "SELECT * FROM glicemia ORDER BY orario ASC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				glicemia.add(new Glicemia(
						rs.getString("CF"),
						rs.getInt("valore"),
						rs.getDate("giorno").toLocalDate(),
						rs.getString("orario"),
						rs.getString("indicazioni")
					));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//CARICA MAIL DAL DATABASE
	public static void loadMailFromDatabase() {
		mail.clear();
		
		String query = "SELECT * FROM mail ORDER BY giorno DESC, orario DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				mail.add(new Mail(
						rs.getInt("id"),
						rs.getString("mittente"),
						rs.getString("destinatario"),
						rs.getString("oggetto"),
						rs.getString("corpo"),
						rs.getDate("giorno").toLocalDate(),
						rs.getTime("orario").toLocalTime(),
						rs.getBoolean("letta")
					));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CARICA PATOLOGIE DAL DATABASE
	public static void loadPatologieFromDatabase() {
		patologie.clear();
		
		String query = "SELECT * FROM patologie ORDER BY nome";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				patologie.add(new Patologia(
						rs.getString("CF"),
						rs.getString("nome"),
						rs.getDate("dataInizio").toLocalDate(),
						rs.getString("indicazioni"),
						rs.getString("modificato")
					));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CARICA TERAPIE CONCOMITANTI DAL DATABASE
	public static void loadTerapieConcomitantiFromDatabase() {
		terapieConcomitanti.clear();
		
		String query = "SELECT * FROM terapieconcomitanti ORDER BY dataInizio DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				terapieConcomitanti.add(new TerapiaConcomitante(
						rs.getString("CF"),
						rs.getString("nome"),
						rs.getDate("dataInizio").toLocalDate(),
						rs.getDate("dataFine").toLocalDate(),
						rs.getString("modificato")
					));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CARICA QUESTIONARIO DAL DATABASE
	public static void loadQuestionarioFromDatabase() {
		questionari.clear();
		
		String query = "SELECT * FROM questionario ORDER BY giornoCompilazione DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				questionari.add(new Questionario(
						rs.getString("CF"),
						rs.getDate("giornoCompilazione").toLocalDate(),
						rs.getString("nomeFarmaco"),
						rs.getInt("dosiGiornaliere"),
						rs.getInt("quantità"),
						rs.getString("sintomi"),
						rs.getBoolean("controllato")
					));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// CREA LISTE PAZIENTI E DIABETOLOGI
	public static void creaListe(){
		pazienti = utenti.stream()
				.filter(utente -> utente.getRuolo().equals("paziente"))
				.toList();
		diabetologi = utenti.stream()
				.filter(utente -> utente.getRuolo().equals("diabetologo"))
				.toList();
	}
	
	// CONTROLLO ESISTENZA UTENTE
	public static boolean utenteEsiste(String cf) {
		return utenti.stream()
			.anyMatch(utente -> utente.getCf().equals(cf));
	}	
	
	// RITORNA UTENTE
	public static Utente getUtente(String cf) {
		return utenti.stream()
				.filter(utente -> utente.getCf().equals(cf))
				.findFirst()
				.orElse(null);
	}
	
	// CONTA LE MAIL NON LETTE RELAVITE A UN CERTO DESTINATARIO
	public static long contatoreMailNonLette() {
		Utente u = Sessione.getInstance().getUtente();
		return mail.stream()
				.filter(mail -> u.getMail().equals(mail.getDestinatario()))
				.filter(mail -> !mail.getLetta())
				.count();
	}
	
	public static void loadFromDatabase() {
		loadUtentiFromDatabase();
		loadTerapieFromDatabase();
		loadGlicemiaFromDatabase();
		loadMailFromDatabase();
		loadFattoriComorbiditàAllergieFromDatabase();
		loadPatologieFromDatabase();
		loadTerapieConcomitantiFromDatabase();
		loadQuestionarioFromDatabase();
		
		creaListe();
	}
}
