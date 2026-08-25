package cinemax;

public class CineMax {
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