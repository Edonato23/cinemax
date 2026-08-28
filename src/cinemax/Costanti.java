package cinemax;

import java.time.format.DateTimeFormatter;

/**
 * Contiene i valori condivisi utilizzati dall'applicazione Cinemax.
 */
public final class Costanti {

    /** Impedisce l'istanziazione della classe delle costanti. */
    private Costanti() {
        // Costruttore privato per impedire l'instanziazione della classe
    }

    /** Separatore utilizzato nei file CSV dell'applicazione. */
    public static final String SEPARATORE_CSV = "§";

    /** Formato utilizzato per date e orari delle proiezioni. */
    public static final DateTimeFormatter FORMATTATORE_DATA_ORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /** Formato utilizzato per le date di nascita. */
    public static final DateTimeFormatter FORMATTATORE_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Nome del file contenente le prenotazioni. */
    public static final String NOME_FILE_PRENOTAZIONI = "prenotazioni.csv";

    /** Nome del file contenente le proiezioni. */
    public static final String NOME_FILE_PROIEZIONI = "proiezioni.csv";

    /** Nome del file contenente gli utenti. */
    public static final String NOME_FILE_UTENTI = "utenti.csv";

    /** Nome del file contenente il log degli errori. */
    public static final String NOME_FILE_LOG = "errori.txt";

    /** Percorso relativo della cartella contenente i dati. */
    public static final String PATH_RELATIVO = "data";
}
