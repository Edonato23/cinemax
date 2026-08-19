package cinemax;

pubblic class Prenotazione {

    private int codice;
    private Proiezione proiezione;
    private Cliente cliente;
    private int numeroPosti;

    public Prenotazione (int idPrenotazione;
                         int utenteId;
                         int idProiezione;
                         int numeroPosti;) {
                if (idPrenotazione <= 0) {
                    throw new IllegalArgumentException("Id prenotazione non valido.")
                }

                if (utenteId <=0) {
                    throw new IllegalArgumentException("Id utente non valido.")
                }

                if (idProiezione <=0) {
                    throw new IllegalArgumentException("Id proiezione non valido.")
                }

                if (numeroPosti <=0) {
                    throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero.")
                }
                this.idPrenotazione = idPrenotazione;
                this.utenteId = utenteId;
                this.idProiezione = idProiezione;
                this.numeroPosti = numeroPosti;
    }

    public Prenotazione(Prenotazione altraPrenotazione) {
        this.idPrenotazione = altraPrenotazione.idPrenotazione;
        this.utenteId = altraPrenotazione.untenteId;
        this.idProiezione = altraPrenotazione.idProiezione;
        this.numeroPosti = altraPrenotazione.numeroPosti;
    }

    public int getIdPrenotazione() {
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

    public static Prenotazione fromCSV() {
        String[] campi = riga.split(";" , -1);
        return new Prenotazione(Integer.parseInt(campi[0]),
                                Integer.parseInt(campi[1]),
                                Integer.parseInt(campi[2]),
                                Integer.parseInt(campi[3]),);
    }
    
    public String toCSV() {
        return String.join(";", String.valueOf(idPrenotazione),
                                String.valueOf(utenteId),
                                String.valueOf(idPrenotazione),
                                String.valueOf(numeroPosti),);
    }

    @Override
    public String toString() {
        return String.format("Prenotazione %d - Utente: %d - Proiezione: %d - Posti: %d",
                             idPrenotazione,
                             utenteId,
                             idProiezione,
                             numeroPosti,);
    
    }
}
