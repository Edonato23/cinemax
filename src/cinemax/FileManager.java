package cinemax;

public class FileManager {
    
    private String dataDirectory;

    public FIleManager(String dataDirectory){
        this.dataDirectory = dataDirectory;
    }
    public List<Proiezione> caricaProiezioni(String nomeFile){
        Path path = Path.of(dataDirectory, nomeFile);
        List<String> righe = Files.readAllLines(path);
        List<Proiezione> proiezioni = new ArrayList<>();
        for(String riga : righe){
            proiezioni.add(Proiezione.fromCSV(riga));
        }
    }

}
