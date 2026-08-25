package cinemax;

public class Prenotazione {

    private int idPrenotazione;
    private int utenteId;
    private int idProiezione;
    private int numeroPosti;

    public Prenotazione (int idPrenotazione, int utenteId, int idProiezione, int numeroPosti) {
                
                if (idPrenotazione <= 0) {
                    throw new IllegalArgumentException("Id prenotazione non valido.");
                }

                if (utenteId <=0) {
                    throw new IllegalArgumentException("Id utente non valido.");
                }

                if (idProiezione <=0) {
                    throw new IllegalArgumentException("Id proiezione non valido.");
                }

                if (numeroPosti <=0) {
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
    }

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public int getId(){
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


    public void setProiezione(int idProiezione) {
        if (idProiezione <=0) {
            throw new IllegalArgumentException("Id proiezione non valido");
        }
        this.idProiezione = idProiezione;
    }

    public void setNumeroPosti(int numeroPosti) {
        if (numeroPosti <=0) {
            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero");

        }
        this.numeroPosti = numeroPosti;
    }

    public static Prenotazione creaPrenotazione(int codice, int utenteId, int idProiezione, int numeroPosti, int postiDisponibili ) {
        
        if(numeroPosti <= 0){
            throw new IllegalArgumentException("Devi prenotare almeno un posto.");
        }

        if(numeroPosti > postiDisponibili) {
            throw new IllegalArgumentException("Non ci sono abbastanza posti disponibili.");
        }
        return new Prenotazione (codice, utenteId, idProiezione, numeroPosti);
    }

    public void modificaPrenotazione(int nuovoIdProiezione, int nuovoNumeroPosti, int postiDisponibili) {
        if(nuovoIdProiezione <=0) {
            throw new IllegalArgumentException("Id proiezione non valido.");
        }

        if(nuovoNumeroPosti <=0) {
            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero.");
        }

        if(nuovoNumeroPosti > postiDisponibili) {
            throw new IllegalArgumentException("Non ci sono abbastanza posti disponibili.");
        }

        this.idProiezione = nuovoIdProiezione;
        this.numeroPosti = nuovoNumeroPosti;
    }

    public void eliminaPrenotazione() {
        this.numeroPosti = 0;
    }

    public void visualizzaPrenotazione() {
        System.out.println("\n=====Prenotazione=====");
        System.out.println("Codice :" + idPrenotazione);
        System.out.println("Id cliente:" + utenteId);
        System.out.println("Id proiezione:" + idProiezione);
        System.out.println("Numero posti:" + numeroPosti);

    }

    @Override
    public String toString() {
        return String.format("Prenotazione %d - Utente: %d - Proiezione: %d - Posti: %d", idPrenotazione, utenteId, idProiezione, numeroPosti);
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
