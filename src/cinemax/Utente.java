package cinemax;

import java.time.LocalDate;
import java.time.Period;

public class Utente {
    public enum Ruolo {
        CLIENTE,
        PROIEZIONISTA,
        BIGLIETTAIO
    }

    private String nome;
    private String cognome;
    private String username;
    private String password;
    private String domicilio;
    private int ruolo;
    private LocalDate dataNascita;
    private int idUtente;

    // #region Costruttori

    public Utente(String nome,
            String cognome,
            String username,
            String password,
            String domicilio,
            int ruolo,
            LocalDate dataNascita) {

        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.idUtente = 0; // TODO: Assegnare un ID univoco all'utente
    }

    // #endregion

    // #region Getter e Setter

    // #region Getter

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNomeCompleto() {
        return this.nome + " " + this.cognome;
    }

    public Ruolo getRuolo() {
        return Ruolo.values()[this.ruolo];
    }

    public int getEta() {
        return Period.between(this.dataNascita, LocalDate.now()).getYears();
    }

    public int getIdUtente() {
        return this.idUtente;
    }

    // #endregion

    // #endregion
}