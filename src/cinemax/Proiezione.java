package cinemax;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/** Rappresenta una proiezione cinematografica e i relativi dati. */
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
     * Crea una proiezione con i dati indicati.
     *
     * @param idProiezione identificativo della proiezione
     * @param dataOraProiezione data e ora della proiezione
     * @param titoloFilm titolo del film
     * @param genere genere del film
     * @param regista regista del film
     * @param anno anno di uscita del film
     * @param durataMinuti durata del film in minuti
     * @param etaMinima età minima richiesta
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
     * Crea una copia della proiezione indicata.
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
     * Restituisce la data e l'ora della proiezione.
     *
     * @return data e ora della proiezione
     */
    public LocalDateTime getDataOraProiezione() {
        return this.dataOraProiezione;
    }

    /**
     * Restituisce il titolo del film.
     *
     * @return titolo del film
     */
    public String getTitoloFilm() {
        return this.titoloFilm;
    }

    /**
     * Restituisce il genere del film.
     *
     * @return genere del film
     */
    public String getGenere() {
        return this.genere;
    }

    /**
     * Restituisce il regista del film.
     *
     * @return regista del film
     */
    public String getRegista() {
        return this.regista;
    }

    /**
     * Restituisce l'anno di uscita del film.
     *
     * @return anno di uscita del film
     */
    public int getAnno() {
        return this.anno;
    }

    /**
     * Restituisce la durata del film.
     *
     * @return durata del film in minuti
     */
    public int getDurataMinuti() {
        return this.durataMinuti;
    }

    /**
     * Restituisce l'età minima richiesta per il film.
     *
     * @return età minima richiesta
     */
    public int getEtaMinima() {
        return this.etaMinima;
    }

    /**
     * Restituisce il prezzo di un biglietto.
     *
     * @return prezzo di un biglietto
     */
    public double getPrezzoBiglietto() {
        return this.prezzoBiglietto;
    }

    /**
     * Restituisce l'identificativo della proiezione.
     *
     * @return identificativo della proiezione
     */
    public int getIdProiezione() {
        return this.idProiezione;
    }

    /**
     * Assegna un nuovo identificativo alla proiezione.
     *
     * @param idProiezione nuovo identificativo
     */
    void setIdProiezione(int idProiezione) {
        this.idProiezione = idProiezione;
    }

    /**
     * Aggiorna la data e l'ora della proiezione.
     *
     * @param dataOraProiezione nuova data e ora della proiezione
     */
    public void setDataOraProiezione(LocalDateTime dataOraProiezione) {
        this.dataOraProiezione = dataOraProiezione;
    }

    /**
     * Aggiorna il titolo del film.
     *
     * @param titoloFilm nuovo titolo del film
     */
    public void setTitolo(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    /**
     * Aggiorna il genere del film.
     *
     * @param genere nuovo genere del film
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * Aggiorna il regista del film.
     *
     * @param regista nuovo regista del film
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * Aggiorna l'anno di uscita del film.
     *
     * @param anno nuovo anno di uscita del film
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * Aggiorna la durata del film.
     *
     * @param durataMinuti nuova durata del film in minuti
     */
    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    /**
     * Aggiorna l'età minima richiesta per il film.
     *
     * @param etaMinima nuova età minima richiesta
     */
    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    /**
     * Aggiorna il prezzo di un biglietto.
     *
     * @param prezzoBiglietto nuovo prezzo di un biglietto
     */
    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    /**
     * Restituisce le informazioni descrittive del film.
     *
     * @return informazioni del film in formato testuale
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
     * @return proiezione ricavata dalla riga
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
            LocalDateTime dataOraProiezione = LocalDateTime.parse(campi[1]);
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
     * Converte la proiezione nel formato CSV utilizzato dall'applicazione.
     *
     * @return riga CSV della proiezione
     */
    public String toCSV() {
        return String.join(Costanti.SEPARATORE_CSV, String.valueOf(idProiezione), dataOraProiezione.toString(),
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
     * Restituisce una descrizione breve della proiezione.
     *
     * @return descrizione formattata della proiezione
     */
    @Override
    public String toString() {
        return String.format(
                "%d. %s - %s - Prezzo: %.2f euro", idProiezione, titoloFilm, dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                prezzoBiglietto);
    }

    public String toStringLong(){
        return String.format(
                "%d. %s - %s - Prezzo: %.2f euro", idProiezione, this.getInfoFilm(), dataOraProiezione.format(Costanti.FORMATTATORE_DATA_ORA),
                prezzoBiglietto);
    }
}
