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
        this.dataNascita = LocalDate.parse(dataNascita, DATE_FORMAT);
        this.ruolo = Ruolo.values()[ruolo];
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome (String nome) {
        this.nome=nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome (String cognome) {
        this.cognome=cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername (String username) {
        this.username=username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword (String password) {
        this.password=password;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio (String domicilio) {
        this.domicilio=domicilio;
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
        return nome + "" + cognome;
    }

    public int getEta() {

            if (dataNascita == null) {
                return 0;
            }
            return Period.between(dataNascita, LocalDate.now()).getYears();
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
    
    public boolean verificaPassword (String password) {
        return this.password != null && this.password.equals(password);
    }
   
    public static Utente registraCliente( int idUtente, int ruolo, String nome, String cognome, String username, String password, String domicilio, String dataNascita) {
        return new Utente(idUtente, nome, cognome, username, password, domicilio, dataNascita, ruolo);
    }

    @Override
    public String toString() {
        return String.format("%d - %s %s - Username: %s - Ruolo: %s", idUtente, nome, cognome, username, ruolo);
    }
}
