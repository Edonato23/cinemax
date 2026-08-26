package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Rappresenta una proiezione cinematografica con i dati del film,
 * l'orario, il prezzo e i requisiti di accesso.
 */
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

    /**
     * Crea una proiezione con i dati specificati.
     *
     * @param idProiezione identificativo della proiezione
     * @param dataOraProiezione data e ora della proiezione
     * @param titoloFilm titolo del film
     * @param genere genere del film
     * @param regista regista del film
     * @param anno anno di uscita del film
     * @param durataMinuti durata del film in minuti
     * @param etaMinima età minima richiesta per il film
     * @param prezzoBiglietto prezzo di un biglietto
     */
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

    /**
     * Crea una copia della proiezione specificata.
     *
     * @param altraProiezione proiezione da copiare
     */
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

    /**
     * @return data e ora della proiezione
     */
    public LocalDateTime getDataOraProiezione() {
        return this.dataOraProiezione;
    }

    /**
     * @return titolo del film
     */
    public String getTitoloFilm() {
        return this.titoloFilm;
    }

    /**
     * @return genere del film
     */
    public String getGenere() {
        return this.genere;
    }

    /**
     * @return regista del film
     */
    public String getRegista() {
        return this.regista;
    }

    /**
     * @return anno di uscita del film
     */
    public int getAnno() {
        return this.anno;
    }

    /**
     * @return durata del film in minuti
     */
    public int getDurataMinuti() {
        return this.durataMinuti;
    }

    /**
     * @return età minima richiesta per il film
     */
    public int getEtaMinima() {
        return this.etaMinima;
    }

    /**
     * @return prezzo di un biglietto
     */
    public double getPrezzoBiglietto() {
        return this.prezzoBiglietto;
    }

    /**
     * @return identificativo della proiezione
     */
    public int getIdProiezione() {
        return this.idProiezione;
    }

    /**
     * @param dataOraProiezione nuova data e ora della proiezione
     */
    public void setDataOraProiezione(LocalDateTime dataOraProiezione) {
        this.dataOraProiezione = dataOraProiezione;
    }

    /**
     * @param titoloFilm nuovo titolo del film
     */
    public void setTitolo(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    /**
     * @param genere nuovo genere del film
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * @param regista nuovo regista del film
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * @param anno nuovo anno di uscita del film
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * @param durataMinuti nuova durata del film in minuti
     */
    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    /**
     * @param etaMinima nuova età minima richiesta per il film
     */
    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    /**
     * @param prezzoBiglietto nuovo prezzo del biglietto
     */
    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce una descrizione sintetica del film.
     *
     * @return informazioni principali del film
     */
    public String getInfoFilm() {
        return String.format(
                "%s (%d) - %s - Regista: %s - Durata: %d minuti - Età minima: %d", titoloFilm, anno, genere, regista,
                durataMinuti, etaMinima);
    }

    /**
     * Crea una proiezione a partire da una riga CSV.
     *
     * @param riga riga CSV da convertire
     * @return proiezione ottenuta dalla riga
     * @throws IllegalArgumentException se la riga non è valida
     */
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

    /**
     * Converte la proiezione nel formato CSV dell'applicazione.
     *
     * @return riga CSV corrispondente alla proiezione
     */
    public String toCSV() {
        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(idProiezione), dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                titoloFilm, genere, regista, String.valueOf(anno), String.valueOf(durataMinuti),
                String.valueOf(etaMinima), String.valueOf(prezzoBiglietto));
    }

    /**
     * Restituisce l'intestazione del file CSV delle proiezioni.
     *
     * @return intestazione CSV
     */
    public final static String header() {
        return String.join(Costanti.SEPARATORE_CSV, "idProiezione", "dataOraProiezione", "titoloFilm", "genereFilm",
                "registaFilm", "annoFilm", "durataMinuti", "etaMinima", "prezzoBiglietto");
    }

    /**
     * Restituisce una rappresentazione breve della proiezione.
     *
     * @return descrizione della proiezione
     */
    @Override
    public String toString() {
        return String.format(
                "%d. %s - %s - Prezzo: %.2f€", idProiezione, titoloFilm, dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                prezzoBiglietto);
    }
}
