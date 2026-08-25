package cinemax;

public class Prenotazione {

    private int idPrenotazione;
    private int utenteId;
    private int idProiezione;
    private int numeroPosti;
    private Proiezione proiezione;

    public Prenotazione(int idPrenotazione, int utenteId, int idProiezione, int numeroPosti) {

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
    }

    public Prenotazione(Prenotazione altraPrenotazione) {
        this.idPrenotazione = altraPrenotazione.idPrenotazione;
        this.utenteId = altraPrenotazione.utenteId;
        this.idProiezione = altraPrenotazione.idProiezione;
        this.numeroPosti = altraPrenotazione.numeroPosti;
        this.proiezione = altraPrenotazione.proiezione;
    }

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public int getId() {
        return idPrenotazione;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public int getIdProiezione() {
        return idProiezione;
    }

    public int getNumeroPosti() {
        return numeroPosti;
    }

    public void setProiezione(Proiezione proiezione) {
        if (proiezione.getIdProiezione() <= 0) {
            throw new IllegalArgumentException("Id proiezione non valido");
        }
        this.idProiezione = proiezione.getIdProiezione();
        this.proiezione = proiezione;
    }

    public void setNumeroPosti(int numeroPosti) {
        if (numeroPosti <= 0) {
            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero");

        }
        this.numeroPosti = numeroPosti;
    }

    @Override
    public String toString() {
        return String.format("%d %s. Film: %s - Posti: %d - Totale: %.2f", this.idPrenotazione,
                this.proiezione.getDataOraProiezione().format(Costanti.FORMATTATORE_DATA_ORA),
                this.proiezione.getTitoloFilm(), this.numeroPosti, this.numeroPosti * this.proiezione.getPrezzoBiglietto());
    }

    public static Prenotazione fromCSV(String riga) {
        if (riga == null || riga.isBlank()) {
            throw new IllegalArgumentException("La riga CSV non può essere nulla o vuota.");
        }

        String[] campi = riga.split(Costanti.SEPARATORE_CSV);
        if (campi.length != 4) {
            throw new IllegalArgumentException(
                    "Riga CSV non valida: attesi 4 campi, trovati " + campi.length + ".");
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

            return new Prenotazione(idPrenotazione, utenteId, idProiezione, numeroPosti);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Uno o più campi numerici non sono validi.", exception);
        }
    }

    public String toCSV() {
        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(idPrenotazione), String.valueOf(utenteId),
                String.valueOf(idProiezione), String.valueOf(numeroPosti));
    }

    public final static String header() {
        return String.join(Costanti.SEPARATORE_CSV, "idPrenotazione", "utenteId", "idProiezione", "numeroPosti");
    }

}
