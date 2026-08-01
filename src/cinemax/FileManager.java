package cinemax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        Path path = Path.of(dataDirectory, nomeFile);
        List<String> righe = Files.readAllLines(path);
        righe.remove(0);
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
     * @throws IOException se si verifica un errore durante la scrittura del file
     */

    public <T> void salva(String nomeFile, List<T> elementi, Function<T, String> convertitore) throws IOException {
    List<String> righe = new ArrayList<>();
    for (T elemento : elementi) {
        righe.add(convertitore.apply(elemento));
    }
    Path path = Path.of(dataDirectory, nomeFile);
    Files.write(path, righe);
    }
    /**
     * Aggiunge un nuovo elemento al file indicato, mantenendo quelli già
     * presenti: carica gli elementi esistenti, vi aggiunge quello nuovo, e
     * salva di nuovo l'intera lista aggiornata nel file.
     *
     * @param nomeFile il nome del file su cui operare, all'interno della cartella dati
     * @param nuovo l'elemento da aggiungere
     * @param daCSV la funzione per convertire una riga di testo in un
     *        oggetto di tipo T (usata per leggere il file)
     * @param aCSV la funzione per convertire un oggetto di tipo T in una
     *        riga di testo (usata per scrivere il file)
     * @throws IOException se si verifica un errore durante la lettura o la scrittura del file
     */
    
    public <T> void aggiungi(String nomeFile, T nuovo, Function<String, T> daCSV, Function<T, String> aCSV) throws IOException {
    List<T> elementi = carica(nomeFile, daCSV);
    elementi.add(nuovo);
    salva(nomeFile, elementi, aCSV);
    }
}
