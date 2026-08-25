package cinemax;

import java.time.format.DateTimeFormatter;

public final class Costanti {
    private Costanti() {
        // Costruttore privato per impedire l'instanziazione della classe
    }

    public static final String SEPARATORE_CSV = "§";
    public static final DateTimeFormatter FORMATTATORE_DATA_ORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter FORMATTATORE_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final String NOME_FILE_PRENOTAZIONI = "prenotazioni.csv";
    public static final String NOME_FILE_PROIEZIONI = "proiezioni.csv";
    public static final String NOME_FILE_UTENTI = "utenti.csv";
    public static final String NOME_FILE_LOG = "errori.txt";
    public static final String PATH_RELATIVO = "../../data";
}
