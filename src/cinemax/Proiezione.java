package cinemax;

import java.time.LocalDateTime;

public class Proiezione {
    
    private LocalDateTime dataOraProiezione;
    private String titoloFilm;
    private String genere;
    private String regista;
    private int anno;
    private int durataMinuti;
    private int etaMinima;
    private double prezzoBiglietto;

    public Proiezione(LocalDateTime dataOraProiezione, String titoloFilm, String genere, String regista, int anno, int durataMinuti, int etaMinima, double prezzoBiglietto) {
        this.dataOraProiezione = dataOraProiezione;
        this.titoloFilm = titoloFilm;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
        this.prezzoBiglietto = prezzoBiglietto;

    }

    public LocalDateTime getDataOraProiezione() {
        return dataOraProiezione;
    }
    public String getTitoloFilm() {
        return titoloFilm;
    }
    public String getGenere() {
        return genere;
    }
    public String getRegista() {
        return regista;
    }
    public int getAnno() {
        return anno;
    }
    public int getDurataMinuti() {
        return durataMinuti;
    }
    public int getEtaMinima() {
        return etaMinima;
    }
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }
    List<String> campi = new ArrayList<>();
    boolean dentroVirgolette = false;
    StringBuilder corrente = new StringBuilder();

    for(int i = 0; i < riga.length; i++){
        char c = riga.charAt(i);
        if(c == '"'){
            dentroVirgolette = !dentroVirgolette;
        }else if(c == ',' && !dentroVirgolette){
            campi.add(corrente.toString());
            corrente.setLength(0);
        }else {
            corrente.append(c);
        }
    }
    campi.add(corrente.toString());
    return campi;

    public static Proiezione fromCSV(String riga){
        list<String> campi = splitCSV(riga);
        return new Proiezione(
            LocalDateTime.parse(campi.get(0)),
            campi.get(1),
            campi.get(2),
            campi.get(3),
            Integer.parseInt(campi.get(4)),
            Integer.parseInt(campi.get(5)),
            Integer.parseInt(campi.get(6)),
            Double.parseDouble(campi.get(7))
        );
    }
}