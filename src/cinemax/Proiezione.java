package cinemax;

import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Proiezione(LocalDateTime dataOraProiezione,  String titoloFilm, String genere,  String regista,  int anno, int durataMinuti,  int etaMinima,  double prezzoBiglietto) {

        this.dataOraProiezione = dataOraProiezione;
        this.titoloFilm = titoloFilm;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
        this.prezzoBiglietto = prezzoBiglietto;
        this.idProiezione = 0; // L'id viene assegnato da MenuManager
    }

    public Proiezione(Proiezione altraProiezione) {
        this.dataOraProiezione = altraProiezione.dataOraProiezione;
        this.titoloFilm = altraProiezione.titoloFilm;
        this.genere = altraProiezione.genere;
        this.regista = altraProiezione.regista;
        this.anno = altraProiezione.anno;
        this.durataMinuti = altraProiezione.durataMinuti;
        this.etaMinima = altraProiezione.etaMinima;
        this.prezzoBiglietto = altraProiezione.prezzoBiglietto;
        this.idProiezione = altraProiezione.idProiezione;
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

    void assegnaId(int idProiezione) {
        this.idProiezione = idProiezione;
    }

    public void setDataOraProiezione(LocalDateTime dataOraProiezione) {
        this.dataOraProiezione = dataOraProiezione;
    }

    public void setTitolo(String titoloFilm) {
        this.titoloFilm = titoloFilm;
    }

    public void setGenere(String genere){
        this.genere = genere;
    }

    publiv void setRegista(String regista) {
        this.regista = regista;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    pulic void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    public void setPrezzoBiglietto(double prezzoBiglietto) {
        this.prezzoBiglietto = prezzoBiglietto;
    }

    public String getInfoFilm() {
        return String.format(
                "%s (%d) - %s - Regista: %s - Durata: %d minuti - Età minima: %d", titoloFilm, anno, genere, regista, durataMinuti, etaMinima);
    }

    private static List<String> splitCSV(String riga) {
        List<String> campi = new ArrayList<>();
        boolean dentroVirgolette = false;
        StringBuilder corrente = new StringBuilder();

        for (int i = 0; i < riga.length(); i++) {
            char c = riga.charAt(i);
            if (c == '"') {
                dentroVirgolette = !dentroVirgolette;
            } else if (c == ',' && !dentroVirgolette) {
                campi.add(corrente.toString());
                corrente.setLength(0);
            } else {
                corrente.append(c);
            }
        }
        campi.add(corrente.toString());
        return campi;
    }

    public static Proiezione fromCSV(String riga) {
        List<String> campi = splitCSV(riga);
        return new Proiezione( LocalDateTime.parse(campi.get(0), formatter), campi.get(1), campi.get(2), campi.get(3), Integer.parseInt(campi.get(4)), Integer.parseInt(campi.get(5)), Integer.parseInt(campi.get(6)), Double.parseDouble(campi.get(7)));
       
        return proiezione;

    }

    public String toCSV() {
        return String.join(",", String.valueOf(idProiezione), dataOraProiezione.toString(), titoloFilm, genere, regista, String.valueOf(anno), String.valueOf(durataMinuti), String.valueOf(etaMinima), String.valueOf(prezzoBiglietto));
    }

    @Override
    public String toString() {
        return String.format(
                    "%d. %s - %s - Prezzo: %.2f€", idProiezione, titoloFilm, dataOraProiezione.format(formatter), prezzoBiglietto);
    }
}
