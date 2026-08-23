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

    public Utente(
            int idUtente,
            String nome,
            String cognome,
            String username,
            String password,
            String domicilio,
            String dataNascita
            int ruolo) {
        
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.domicilio = domicilio;
        this.dataNascita = dataNascita;
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

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita() {
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

            if (dataNascita == null || dataNascita.isBlank()) {
                return 0;
            }
            try {
                LocalDate nascita = LocalDate.parse(dataNacita, DATE_FORMAT);

                return Period.between(nascita, LocalDate.now()).getYears();
            } catch (DateTimeParseException e) {
                return 0;
            }
    }

    public boolean verificaPassword (String password) {
        return this.password.equals(password);
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

    
    public static Utente fromCVS(String riga) {
            String[] campi = riga.split(";", -1);

            return new Utente (
                    Integer.parseInt(campi[0]),
                    campi[1],
                    campi[2],
                    campi[3],
                    campi[4],
                    campi[5],
                    Integer.parseInt(campi[7]));
    }

    public String toCVS() {
        return String.join(";",String.valueOf(idUtente),
                               nome,
                               cognome,
                               username,
                               password,
                               domicilio,
                               dataNascita == null ? "" : dataNascita.toString(),
                               String.valueOf(ruolo.ordinal())
        );
    }

    @Override
    public String toString() {
        return String.format("%d - %s %s - Username: %s - Ruolo: %s", 
                             idUtente,
                             nome,
                             cognome,
                             username,
                             ruolo,
        );
    }
}
