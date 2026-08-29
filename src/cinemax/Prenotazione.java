package cinemax;

/**
 * Rappresenta una prenotazione effettuata da un utente per una proiezione.
 */
public class Prenotazione {

    /** Identificativo della prenotazione. */
    private int idPrenotazione;
    /** Identificativo dell'utente che ha effettuato la prenotazione. */
    private int utenteId;
    /** Identificativo della proiezione prenotata. */
    private int idProiezione;
    /** Numero di posti prenotati. */
    private int numeroPosti;
    /** Proiezione associata alla prenotazione. */
    private Proiezione proiezione;
    /** Codice breve univoco della prenotazione. */
    private String codiceUnivoco;

    /**
     * Crea una prenotazione validando gli identificativi e il numero di posti.
     *
     * @param idPrenotazione identificativo della prenotazione
     * @param utenteId identificativo dell'utente
     * @param idProiezione identificativo della proiezione
     * @param numeroPosti numero di posti da prenotare
     * @param codiceUnivoco codice univoco della prenotazione
     * @throws IllegalArgumentException se un identificativo o il numero di posti non è valido
     */
    public Prenotazione(int idPrenotazione, int utenteId, int idProiezione, int numeroPosti, String codiceUnivoco) {

        if (idPrenotazione <= 0) {
            throw new IllegalArgumentException("Id prenotazione non valido.");
        }

        if (utenteId <= 0) {
            throw new IllegalArgumentException("Id utente non valido.");
        }

        if (idProiezione <= 0) {
            throw new IllegalArgumentException("Id proiezione non valido.");
        }

        if (numeroPosti <= 0) {
            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero.");
        }
        this.idPrenotazione = idPrenotazione;
        this.utenteId = utenteId;
        this.idProiezione = idProiezione;
        this.numeroPosti = numeroPosti;
        this.codiceUnivoco = codiceUnivoco;
    }

    /**
     * Crea una copia di una prenotazione esistente.
     *
     * @param altraPrenotazione prenotazione da copiare
     */
    public Prenotazione(Prenotazione altraPrenotazione) {
        this.idPrenotazione = altraPrenotazione.idPrenotazione;
        this.utenteId = altraPrenotazione.utenteId;
        this.idProiezione = altraPrenotazione.idProiezione;
        this.numeroPosti = altraPrenotazione.numeroPosti;
        this.proiezione = altraPrenotazione.proiezione;
        this.codiceUnivoco = altraPrenotazione.codiceUnivoco;
    }

    /**
     * Restituisce l'identificativo della prenotazione.
     *
     * @return identificativo della prenotazione
     */
    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    /**
     * Restituisce l'identificativo della prenotazione come alias di {@link #getIdPrenotazione()}.
     *
     * @return identificativo della prenotazione
     */
    public int getId() {
        return idPrenotazione;
    }

    /**
     * Restituisce l'identificativo dell'utente.
     *
     * @return identificativo dell'utente
     */
    public int getUtenteId() {
        return utenteId;
    }

    /**
     * Restituisce l'identificativo della proiezione.
     *
     * @return identificativo della proiezione
     */
    public int getIdProiezione() {
        return idProiezione;
    }

    /**
     * Restituisce il numero di posti prenotati.
     *
     * @return numero di posti
     */
    public int getNumeroPosti() {
        return numeroPosti;
    }

    /**
     * Restituisce il codice univoco della prenotazione.
     *
     * @return codice univoco
     */
    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    /**
     * Associa una proiezione alla prenotazione.
     *
     * @param proiezione proiezione da associare
     * @throws IllegalArgumentException se l'identificativo della proiezione non è valido
     */
    public void setProiezione(Proiezione proiezione) {
        if (proiezione.getIdProiezione() <= 0) {
            throw new IllegalArgumentException("Id proiezione non valido");
        }
        this.idProiezione = proiezione.getIdProiezione();
        this.proiezione = proiezione;
    }

    /**
     * Aggiorna il numero di posti prenotati.
     *
     * @param numeroPosti nuovo numero di posti
     * @throws IllegalArgumentException se il numero di posti non è maggiore di zero
     */
    public void setNumeroPosti(int numeroPosti) {
        if (numeroPosti <= 0) {
            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero");

        }
        this.numeroPosti = numeroPosti;
    }

    /**
     * Restituisce una descrizione della prenotazione.
     *
     * @return descrizione della prenotazione con codice, data, film, posti e totale
     */
    @Override
    public String toString() {
        return String.format("%d %s %s. Film: %s (%d) - Posti: %d - Totale: %.2f", this.idPrenotazione, this.codiceUnivoco,
                this.proiezione.getDataOraProiezione().format(Costanti.FORMATTATORE_DATA_ORA),
                this.proiezione.getTitoloFilm(), this.proiezione.getAnno(), this.numeroPosti,
                this.numeroPosti * this.proiezione.getPrezzoBiglietto());
    }

    /**
     * Crea una prenotazione a partire da una riga CSV.
     *
     * @param riga riga CSV da convertire
     * @return prenotazione ottenuta dalla riga
     * @throws IllegalArgumentException se la riga o uno dei suoi campi non è valido
     */
    public static Prenotazione fromCSV(String riga) {
        if (riga == null || riga.isBlank()) {
            throw new IllegalArgumentException("La riga CSV non può essere nulla o vuota.");
        }

        String[] campi = riga.split(Costanti.SEPARATORE_CSV);
        if (campi.length != 5) {
            throw new IllegalArgumentException(
                    "Riga CSV non valida: attesi 5 campi, trovati " + campi.length + ".");
        }

        for (int indice = 0; indice < campi.length; indice++) {
            campi[indice] = campi[indice].trim();
            if (campi[indice].isEmpty()) {
                throw new IllegalArgumentException("Il campo " + (indice + 1) + " non può essere vuoto.");
            }
        }

        try {
            int idPrenotazione = Integer.parseInt(campi[0]);
            int utenteId = Integer.parseInt(campi[1]);
            int idProiezione = Integer.parseInt(campi[2]);
            int numeroPosti = Integer.parseInt(campi[3]);
            String codiceUnivoco = campi[4];

            return new Prenotazione(idPrenotazione, utenteId, idProiezione, numeroPosti, codiceUnivoco);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Uno o più campi numerici non sono validi.", exception);
        }
    }

    /**
     * Converte la prenotazione nel formato CSV dell'applicazione.
     *
     * @return riga CSV corrispondente alla prenotazione
     */
    public String toCSV() {
        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(idPrenotazione), String.valueOf(utenteId),
                String.valueOf(idProiezione), String.valueOf(numeroPosti), codiceUnivoco);
    }

    /**
     * Restituisce l'intestazione del file CSV delle prenotazioni.
     *
     * @return intestazione CSV
     */
    public final static String header() {
        return String.join(Costanti.SEPARATORE_CSV, "idPrenotazione", "utenteId", "idProiezione", "numeroPosti", "codiceUnivoco");
    }

}
