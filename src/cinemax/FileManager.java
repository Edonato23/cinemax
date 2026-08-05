package cinemax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
     * Carica dal file indicato una lista di elementi di tipo generico T,
     * convertendo ogni riga di testo tramite la funzione fornita.
     *
     * @param nomeFile il nome del file da leggere, all'interno della cartella dati
     * @param convertitore la funzione che trasforma una riga di testo in un
     *        oggetto di tipo T (ad esempio Proiezione::fromCSV)
     * @return la lista degli elementi letti dal file
     * @throws IOException se si verifica un errore durante la lettura del file
     */

    public <T> List<T> carica(String nomeFile, Function<String, T> convertitore) throws IOException {
        Objects.requireNonNull(convertitore, "Il convertitore non può essere vuoto");

        Path path = Path.of(dataDirectory, nomeFile);
        List<String> righe = Files.readAllLines(path);

        // Controlla che le righe non siano vuote e procedere a rimuovere la riga dell'header
        if(!righe.isEmpty())
        {
            righe.remove(0);
        }

        List<T> risultato = new ArrayList<>();
        for(String riga : righe){
        risultato.add(convertitore.apply(riga));
        }

        return risultato;
    }

        /**
     * Salva la lista di elementi fornita nel file indicato, sovrascrivendo
     * il contenuto precedente. Ogni elemento viene trasformato in una riga
     * di testo usando la funzione passata come parametro.
     *
     * @param nomeFile il nome del file su cui scrivere, all'interno della cartella dati
     * @param elementi la lista degli elementi da salvare
     * @param convertitore la funzione che trasforma un elemento di tipo T in
     *        una riga di testo da scrivere nel file (ad esempio Proiezione::toCSV)
     * @param intestazione la riga di intestazione da scrivere come prima riga del file
     * @throws IOException se si verifica un errore durante la scrittura del file
     * @throws NullPointerException se la lista degli elementi o il convertitore sono nulli
     */

    public <T> void salva(String nomeFile, List<T> elementi, Function<T, String> convertitore, String intestazione) throws IOException {
        Objects.requireNonNull(elementi, "La lista degli elementi non può essere nulla");
        Objects.requireNonNull(convertitore, "Il convertitore non può essere nullo");
        List<String> righe = new ArrayList<>(elementi.size());
        righe.add(intestazione);
        for (T elemento : elementi) {
            righe.add(convertitore.apply(elemento));
        }

        Path path = Path.of(dataDirectory, nomeFile);
        Files.write(path, righe);
    }
}
