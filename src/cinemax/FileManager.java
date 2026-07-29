package cinemax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * gestisce la lettura e la scrittura su file dei dati delle proiezioni 
 * del cinema, permettendo di caricarle, salvarle e aggiungerne di nuove.
 * @author Edoardo Donato  
 */
    public class FileManager {
    
    private String dataDirectory;
/**
 * crea un nuovo file manager che opera sulla cartella specificata 
 * 
 * @param dataDirectory il percorso della cartella contenente i file di dati
 */
    public FileManager(String dataDirectory){
        this.dataDirectory = dataDirectory;
    }
    /**
     * carica tutte le proiezioni salvate nel file indicato 
     * @param nomeFile il nome del file da leggere, all'interno della cartella dati
     * @return la lista delle proiezioni lette dal file
     * @throws IOException se si verifica un errore durante la lettura del file
     */
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
    /**
     * salva la lista di proiezioni fornita, sovrascrivendo il file indicato 
     * @param nomeFile il nome del file su cui scrivere, all'interno della cartella dati
     * @param proiezioni la lista completa delle proiezioni da salvare
     * @throws IOException se si verifica un errore durante la scrittura del file
     */
    public void salvaProiezioni(String nomeFile, List<Proiezione> proiezioni) throws IOException {
        List<String> righe = new ArrayList<>();
        for(Proiezione p : proiezioni){
            righe.add(p.toCSV());
        }
        Path path = Path.of(dataDirectory, nomeFile);
        Files.write(path, righe);
    }
    /**
     * aggiunge una nuova proiezione al file indicato, mantenendo quelle già 
     * presenti: carica le proiezioni esistenti, vi aggiunge quella nuova, e 
     * salva di nuovo l'intera lista aggiornata nel file.
     * @param nomeFile il nome del file su cui operare, all'interno della cartella dati
     * @param nuova la nuova proiezione da aggiungere
     * @throws IOException se si verifica un errore durante la lettura o la scrittura del file
     */
    public void aggiungiProiezione(String nomeFile, Proiezione nuova) throws IOException{
        List<Proiezione> proiezioni = caricaProiezioni(nomeFile);
        proiezioni.add(nuova);
        salvaProiezioni(nomeFile, proiezioni);
    }
}
