package cinemax;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

/**
 * Rappresenta un utente registrato nell'applicazione Cinemax.
 */
public class Utente {
    
    /** Ruoli disponibili per gli utenti dell'applicazione. */
    public enum Ruolo {
        /** Utente che può effettuare prenotazioni. */
        CLIENTE,
        /** Utente che gestisce le proiezioni. */
        PROIEZIONISTA,
        /** Utente che gestisce le prenotazioni alla biglietteria. */
        BIGLIETTAIO
    }
   
    /** Identificativo dell'utente. */
    private int idUtente;
    /** Nome dell'utente. */
    private String nome;
    /** Cognome dell'utente. */
    private String cognome;
    /** Nome utente utilizzato per l'accesso. */
    private String username;
    /** Password dell'utente, memorizzata in forma cifrata. */
    private String password;
    /** Domicilio dell'utente. */
    private String domicilio;
    /** Data di nascita dell'utente, se disponibile. */
    private LocalDate dataNascita;
    /** Ruolo assegnato all'utente. */
    private Ruolo ruolo;
 
    /**
     * Crea un utente con i dati specificati.
     *
     * @param idUtente identificativo dell'utente
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param username nome utente per l'accesso
     * @param password password dell'utente
     * @param domicilio domicilio dell'utente
     * @param dataNascita data di nascita nel formato previsto
     * @param ruolo indice del ruolo nell'enumerazione {@link Ruolo}
     */
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
            this.dataNascita = LocalDate.parse(dataNascita, Costanti.FORMATTATORE_DATA);
        }
        
        this.ruolo = Ruolo.values()[ruolo];
    }
    
    /**
     * Crea una copia di un utente esistente.
     *
     * @param altroUtente utente da copiare
     */
    public Utente(Utente altroUtente){
        this.idUtente = altroUtente.idUtente;
        this.nome = altroUtente.nome;
        this.cognome = altroUtente.cognome;
        this.username = altroUtente.username;
        this.password = altroUtente.password;
        this.domicilio = altroUtente.domicilio;
        this.dataNascita = altroUtente.dataNascita;
        this.ruolo = altroUtente.ruolo;
    }

    /**
     * @return identificativo dell'utente
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * @return nome dell'utente
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome nuovo nome dell'utente
     */
    public void setNome (String nome) {
        this.nome = nome;
    }

    /**
     * @return cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome nuovo cognome dell'utente
     */
    public void setCognome (String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return username dell'utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username nuovo username dell'utente
     */
    public void setUsername (String username) {
        this.username = username;
    }

    /**
     * @return password dell'utente
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password nuova password dell'utente
     */
    public void setPassword (String password) {
        this.password = password;
    }

    /**
     * @return domicilio dell'utente
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * @param domicilio nuovo domicilio dell'utente
     */
    public void setDomicilio (String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * @return data di nascita dell'utente, oppure {@code null}
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * @param dataNascita nuova data di nascita
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @return ruolo dell'utente
     */
    public Ruolo getRuolo() {
        return ruolo;
    }

    /**
     * @param ruolo nuovo ruolo dell'utente
     */
    public void setRuolo (Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    /**
     * @return nome e cognome concatenati
     */
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    /**
     * Calcola l'età dell'utente in base alla data di nascita.
     *
     * @return età in anni, oppure zero se la data di nascita non è disponibile
     */
    public int getEta() {
        if (this.dataNascita == null) {
                return 0;
        }
        return Period.between(dataNascita, LocalDate.now()).getYears();
    }

    /**
     * Crea un utente a partire da una riga CSV.
     *
     * @param riga riga CSV da convertire
     * @return utente ottenuto dalla riga
     * @throws IllegalArgumentException se la riga o uno dei suoi campi non è valido
     */
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

    /**
     * Converte l'utente nel formato CSV dell'applicazione.
     *
     * @return riga CSV corrispondente all'utente
     */
    public String toCSV() {
        String dataNascitaCSV = this.dataNascita == null ? " " : this.dataNascita.format(Costanti.FORMATTATORE_DATA);

        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(this.idUtente), this.nome, this.cognome, this.username, this.password, this.domicilio, dataNascitaCSV, String.valueOf(this.ruolo.ordinal()));
    }

    /**
     * Restituisce l'intestazione del file CSV degli utenti.
     *
     * @return intestazione CSV
     */
    public static final String header() {
        return String.join(Costanti.SEPARATORE_CSV,"idUtente", "nome", "cognome", "username", "password", "domicilio", "dataNascita", "ruolo");
    }

    /**
     * Restituisce una descrizione sintetica dell'utente.
     *
     * @return descrizione dell'utente
     */
    @Override
    public String toString() {
        return String.format("%d - %s %s - Username: %s - Ruolo: %s", idUtente, nome, cognome, username, ruolo);
    }
}
