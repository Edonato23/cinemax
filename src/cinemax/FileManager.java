package cinemax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    
    private String dataDirectory;

    public FileManager(String dataDirectory){
        this.dataDirectory = dataDirectory;
    }
    public List<Proiezione> caricaProiezioni(String nomeFile) throws IOException {
        Path path = Path.of(dataDirectory, nomeFile);
        List<String> righe = Files.readAllLines(path);
        righe.remove(0);
        List<Proiezione> proiezioni = new ArrayList<>();
        for(String riga : righe){
            proiezioni.add(Proiezione.fromCSV(riga));
        }
        return proiezioni;
    }

    public void salvaProiezioni(String nomeFile, List<Proiezione> proiezioni) throws IOException {
        List<String> righe = new ArrayList<>();
        for(Proiezione p : proiezioni){
            righe.add(p.toCSV());
        }
        Path path = Path.of(dataDirectory, nomeFile);
        Files.write(path, righe);
    }
}
