package cinemax;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.time.LocalDateTime;

/**
 * Gestisce la lettura, la scrittura e la registrazione degli errori nei file
 * dell'applicazione Cinemax.
 *
 * @author Edoardo Donato
 */
public class FileManager {
    
    /** Percorso della cartella che contiene i file dell'applicazione. */
    private String dataDirectory;

    /**
     * Crea un file manager che opera sulla cartella specificata.
     *
     * @param dataDirectory percorso della cartella contenente i file di dati
     * @throws IllegalArgumentException se il percorso è nullo o vuoto
     */
    public FileManager(String dataDirectory) {
        if (dataDirectory == null || dataDirectory.isBlank()) {
            throw new IllegalArgumentException("La cartella dei dati non può essere vuota.");
        }
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
    * @throws NullPointerException se il nome del file o il convertitore è nullo
    * @throws IllegalArgumentException se il nome del file non è valido
     */

    public <T> List<T> carica(String nomeFile, Function<String, T> convertitore) throws IOException {
        Objects.requireNonNull(nomeFile, "Il nome del file non può essere nullo");
        Objects.requireNonNull(convertitore, "Il convertitore non può essere vuoto");

        Path path = pPercorsoFile(nomeFile);
        if (!Files.isRegularFile(path)) {
            return new ArrayList<>();
        }
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
    * @throws IllegalArgumentException se il nome del file non è valido
     */

    public <T> void salva(String nomeFile, List<T> elementi, Function<T, String> convertitore, String intestazione) throws IOException {
        Objects.requireNonNull(nomeFile, "Il nome del file non può essere nullo");
        Objects.requireNonNull(elementi, "La lista degli elementi non può essere nulla");
        Objects.requireNonNull(convertitore, "Il convertitore non può essere nullo");
        Objects.requireNonNull(intestazione, "L'intestazione non può essere nulla");
        List<String> righe = new ArrayList<>(elementi.size());
        righe.add(intestazione);
        for (T elemento : elementi) {
            righe.add(convertitore.apply(elemento));
        }

        Path path = pPercorsoFile(nomeFile);
        Files.createDirectories(path.getParent());
        Files.write(path, righe, StandardCharsets.UTF_8);
    }

    /**
     * Registra nel file di log i dati e lo stack trace di un'eccezione.
     *
     * @param exception eccezione da registrare
     * @throws IOException se si verifica un errore durante la scrittura del log
     * @throws NullPointerException se l'eccezione è nulla
     * @throws IllegalArgumentException se il percorso del file di log non è valido
     */
    public void registraErrore(Exception exception) throws IOException {
        Objects.requireNonNull(exception, "L'eccezione non può essere nulla");

        Path path = pPercorsoFile(Costanti.NOME_FILE_LOG);
        Files.createDirectories(path.getParent());

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        StringBuilder record = new StringBuilder();
        record.append("========================================\n");
        record.append("Data e ora: ").append(LocalDateTime.now()).append("\n");
        record.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
        record.append("Tipo: ").append(exception.getClass().getName()).append("\n");
        record.append("Messaggio: ").append(exception.getMessage()).append("\n");
        record.append("Stack trace:\n").append(stackTrace);
        record.append("========================================\n\n");

        Files.writeString(path, record.toString(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND);
    }

    /**
     * Costruisce e valida il percorso di un file contenuto nella cartella dati.
     *
     * @param nomeFile nome del file da risolvere
     * @return percorso normalizzato del file
     * @throws IllegalArgumentException se il percorso esce dalla cartella dati
     */
    private Path pPercorsoFile(String nomeFile) {
        Path path = Path.of(dataDirectory, nomeFile).normalize();
        Path cartellaDati = Path.of(dataDirectory).toAbsolutePath().normalize();

        if (!path.toAbsolutePath().normalize().startsWith(cartellaDati)) {
            throw new IllegalArgumentException("Il nome del file non è valido: " + nomeFile);
        }

        return path;
    }
}
