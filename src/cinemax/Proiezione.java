package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Proiezione {

    private LocalDateTime dataOraProiezione;
    private String titoloFilm;
    private String genere;
    private String regista;
    private int anno;
    private int durataMinuti;
    private int etaMinima;
    private double prezzoBiglietto;
    private int idProiezione;

    // Costruttore con parametri
    public Proiezione(int idProiezione, LocalDateTime dataOraProiezione, String titoloFilm, String genere, String regista, int anno,
            int durataMinuti, int etaMinima, double prezzoBiglietto) {

        this.idProiezione = idProiezione;
        this.dataOraProiezione = dataOraProiezione;
        this.titoloFilm = titoloFilm;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
        this.prezzoBiglietto = prezzoBiglietto;
        //this.idProiezione = 0; // L'id viene assegnato da MenuManager
    }

    // Costruttore copia
    public Proiezione(Proiezione altraProiezione) {
        this.idProiezione = altraProiezione.idProiezione;
        this.dataOraProiezione = altraProiezione.dataOraProiezione;
        this.titoloFilm = altraProiezione.titoloFilm;
        this.genere = altraProiezione.genere;
        this.regista = altraProiezione.regista;
        this.anno = altraProiezione.anno;
        this.durataMinuti = altraProiezione.durataMinuti;
        this.etaMinima = altraProiezione.etaMinima;
        this.prezzoBiglietto = altraProiezione.prezzoBiglietto;
    }

    public LocalDateTime getDataOraProiezione() {
        return this.dataOraProiezione;
    }

    public String getTitoloFilm() {
        return this.titoloFilm;
    }

    public String getGenere() {
        return this.genere;
    }

    public String getRegista() {
        return this.regista;
    }

    public int getAnno() {
        return this.anno;
    }

    public int getDurataMinuti() {
        return this.durataMinuti;
    }

    public int getEtaMinima() {
        return this.etaMinima;
    }

    public double getPrezzoBiglietto() {
        return this.prezzoBiglietto;
    }

    public int getIdProiezione() {
        return this.idProiezione;
    }

    public void setDataOraProiezione(LocalDateTime dataOraProiezione) {
        this.dataOraProiezione = dataOraProiezione;
    }

    public void setTitolo(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public void setRegista(String regista) {
        this.regista = regista;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    public String getInfoFilm() {
        return String.format(
                "%s (%d) - %s - Regista: %s - Durata: %d minuti - Età minima: %d", titoloFilm, anno, genere, regista,
                durataMinuti, etaMinima);
    }

    public static Proiezione fromCSV(String riga) {
        if (riga == null || riga.isBlank()) {
            throw new IllegalArgumentException("La riga CSV non può essere nulla o vuota.");
        }

        String[] campi = riga.split(Costanti.SEPARATORE_CSV);
        if (campi.length != 9) {
            throw new IllegalArgumentException(
                    "Riga CSV non valida: attesi 9 campi, trovati " + campi.length + ".");
        }

        for (int indice = 0; indice < campi.length; indice++) {
            campi[indice] = campi[indice].trim();
            if (campi[indice].isEmpty()) {
                throw new IllegalArgumentException("Il campo " + (indice + 1) + " non può essere vuoto.");
            }
        }

        try {
            int idProiezione = Integer.parseInt(campi[0]);
            LocalDateTime dataOraProiezione = LocalDateTime.parse(campi[1], Costanti.FORMATTATORE_DATA_ORA);
            int anno = Integer.parseInt(campi[5]);
            int durataMinuti = Integer.parseInt(campi[6]);
            int etaMinima = Integer.parseInt(campi[7]);
            double prezzoBiglietto = Double.parseDouble(campi[8]);

            return new Proiezione(idProiezione, dataOraProiezione, campi[2], campi[3], campi[4], anno,
                    durataMinuti, etaMinima, prezzoBiglietto);
        } catch (NumberFormatException | DateTimeParseException exception) {
            throw new IllegalArgumentException("Uno o più campi numerici o la data non sono validi.", exception);
        }

    }

    public String toCSV() {
        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(idProiezione), dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                titoloFilm, genere, regista, String.valueOf(anno), String.valueOf(durataMinuti),
                String.valueOf(etaMinima), String.valueOf(prezzoBiglietto));
    }

    public final static String header() {
        return String.join(Costanti.SEPARATORE_CSV, "idProiezione", "dataOraProiezione", "titoloFilm", "genereFilm",
                "registaFilm", "annoFilm", "durataMinuti", "etaMinima", "prezzoBiglietto");
    }

    @Override
    public String toString() {
        return String.format(
                "%d. %s - %s - Prezzo: %.2f€", idProiezione, titoloFilm, dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                prezzoBiglietto);
    }
}
