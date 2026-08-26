package cinemax;

/**
 * Punto di ingresso dell'applicazione Cinemax.
 */
public class CineMax {
    /**
     * Avvia l'applicazione, inizializza la gestione dei file e apre il menu principale.
     * Gli errori vengono registrati nel file di log quando possibile.
     *
     * @param args argomenti passati da riga di comando
     */
    public static void main(String[] args) {
        FileManager fileManager = new FileManager(Costanti.PATH_RELATIVO);

        try {
            MenuManager menuManager = new MenuManager(fileManager);
            menuManager.Menu();
        } catch (Exception exception) {
            try {
                fileManager.registraErrore(exception);
            } catch (Exception logException) {
                System.err.println("Impossibile salvare il log: " + logException.getMessage());
            }

            System.err.println("Errore durante l'esecuzione: " + exception.getMessage());
        }
    }
}