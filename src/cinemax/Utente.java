package cinemax;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utente {
    
    public enum Ruolo {
        CLIENTE,
        PROIEZIONISTA,
        BIGLIETTAIO
    }
   
    private int idUtente;
    private String nome;
    private String cognome;
    private String username;
    private String password;
    private String domicilio;
    private LocalDate dataNascita;
    private Ruolo ruolo;
 
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern ("dd/MM/yyyy");

    public Utente(int idUtente, String nome, String cognome, String username, String password, String domicilio, String dataNascita, int ruolo) {
        
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.domicilio = domicilio;
        
        if (dataNascita == null || dataNascita.isBlank()) {
            this.dataNascita = null;
        }else{
            this.dataNascita = LocalDate.parse(dataNascita, DATE_FORMAT);
        }
        
        this.ruolo = Ruolo.values()[ruolo];
    }
    
    public Utente(Utente altroUtente){
        this.idUtente = altro.idUtente.idUtente;
        this.nome = altroUtente.nome;
        this.cognome = altroUtente.cognome;
        this.username = altroUtente.username;
        this.password = altroUtente.password;
        this.domincilio = altroUtente.domicilio;
        this.dataNascita = altroUtente.dataNascita;
        this.ruolo = altroUtente.ruolo;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome (String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome (String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio (String domicilio) {
        this.domicilio = domicilio;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo (Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    public int getEta() {
        if (this.dataNascita == null) {
                return 0;
        }
        return Period.between(dataNascita, LocalDate.now()).getYears();
    }

    public boolean verificaPassword (String password) {
        return this.password != null && this.password.equals(password);
    }

    public boolean isCliente() {
        return this.ruolo == Ruolo.CLIENTE;
    }

    public boolean isProiezionista() {
        return this.ruolo == Ruolo.PROIEZIONISTA;
    }

    public boolean isBigliettaio() {
        return this.ruolo == Ruolo.BIGLIETTAIO;
    }

    
    public static Utente registraCliente(int idUtente, String nome, Striing cognome, String username, String password, String domicilio, String dataNascita) {
                     
        return new Utente(idUtente, nome, cognome, username, password, domicilio, dataNascita);
    }

    public static Utente fromCSV(String riga) {

        if (riga == null || riga.isBlank()) {
            throw new IllegalArgumentException("La riga CSV non puo essere nulla o vuota.");
        }

        String[] campi = riga.split(Costanti.SEPARATORE_CSV);

        if (campi.length != 8) {
            throw new IllegalArgumentException("Riga CSV non valida.");
        }

        for (int i = 0; i < campi.length; i++) {
            campi[i] = campi[i].trim();
            
            if (campi[i].isEmpty() && i != 6) {
                throw new IllegalArgumentException("il campo" + (i + 1) + "non puo essere vuoto.");
            }
        }

        try{
            int idUtente = Integer.parseInt(campi[0]);
            int ruolo = Integer.parseInt(campi[7]);

            if (ruolo < 0 || ruolo >= Ruolo.values().length) {
                throw new IllegalArgumentException("Ruolo non valido:" + ruolo);
            }

            return new Utente(idUtente, campi[1], campi[2], campi[3], campi[4], campi[5], campi[6], ruolo);
        } catch (NumberFormatException | DateTimeParseException exception) {
            throw new IllegalArgumentException("Uno o piu campi numerici oppure la data non sono validi.", exception);
        }
    }

    public String toCSV() {
        String dataNascitaCSV = this.dataNascita == null ? " " : this.dataNascita.format(DATE_FORMAT);

        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(this.idUtente), this.nome, this.cognome, this.username, this.password, this.domicilio, dataNascitaCSV, String.valueOf(this.ruolo.ordinal()));
    }

    public final String header() {
        return String.join(Costanti.SEPARATORE_CSV,"idUtente", "nome", "cognome", "username", "password", "domicilio", "dataNascita", "ruolo");
    }

    @Override
    public String toString() {
        return String.format("%d - %s %s - Username: %s - Ruolo: %s", idUtente, nome, cognome, username, ruolo);
    }
}
