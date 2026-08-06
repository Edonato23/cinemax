package cinemax;

import java.time.LocalDate;

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

    // #endregion

    // #endregion
}