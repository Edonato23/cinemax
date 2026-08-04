package cinemax;

import java.io.IOException;
import java.util.Scanner;

public class MenuManager {
    private static Scanner pScanner = new Scanner(System.in);

    // Metodo per pulire la console
    // private static void PulisciConsole()
    // {
    // try{
    // new ProcessBuilder("cmd", "/c", "cls")
    // .inheritIO()
    // .start()
    // .waitFor();
    // } catch (IOException | InterruptedException e) {
    // System.out.println("Errore durante la pulizia della console: " +
    // e.getMessage());
    // }
    // }

    public static void MenuPrincipale() {
        // Dichiarazione e inizializzazione delle variabili locali
        Boolean checkOk;
        int inputParsato = 0;

        System.out.println("================================");
        System.out.println("|                              |");
        System.out.println("|      BENVENUTO AL CINEMA     |");
        System.out.println("|                              |");
        System.out.println("================================");

        System.out.println("\n\nCosa vuoi fare?");

        do {
            checkOk = true;
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("3. Continua come ospite");

            // Legge il valore dell'input
            if(checkOk) inputParsato = pLeggiIntero();

            if (checkOk) {
                switch (inputParsato) {
                    case 1:
                        // Login
                        MenuManager.pLogin();
                        break;
                    case 2:
                        // Registrazione
                        MenuManager.pRegistrazione();
                        break;
                    case 3:
                        // Guest
                        MenuManager.pMenuGuest();
                        break;
                    default:
                        System.out.println("Scelta non valida");
                        checkOk = !checkOk;
                        break;
                }
            }

        } while (!checkOk);

    }

    // #region Metodi privati

    private static void pLogin() {
        String ruoloUtente = "";
        String username = "";
        String password = "";
        Boolean checkOk = true;

        System.out.println("\n\n===== LOGIN =====");

        do {
            checkOk = true;
            if (checkOk) {
                System.out.println("Inserisci il tuo username:");
                username = pScanner.next();
                System.out.println("Inserisci la tua password:");
                password = pScanner.next();
            }

            // Recupero dell'utente

            // if(utente != null) (da gestire poi con enum)

            if (checkOk) {
                switch (ruoloUtente) {
                    case "Cliente":
                        break;
                    case "Bigliettaio":
                        break;
                    case "Proiezionista":
                        break;
                }
            }
        } while (!checkOk);

    }

    private static void pRegistrazione() {
        String nome = "";
        String cognome = "";
        String username = "";
        String password = "";
        String domicilio = "";
        String ruolo = "";
        var dataNascita = "";

        Boolean checkOk = true;

        System.out.println("\n\n===== REGISTRAZIONE =====");

        do {
            checkOk = true;
            if (checkOk) {
                System.out.println("Inserisci il nome:");
                nome = pScanner.nextLine();
                System.out.println("Inserisci il cognome:");
                cognome = pScanner.nextLine();
                System.out.println("Inserisci username:");
                username = pScanner.next();
                System.out.println("Inserisci password:");
                password = pScanner.next();
                System.out.println("Inserisci data di nascita (formato: dd/MM/yyyy) (facoltativo):");
                dataNascita = pScanner.next();
                System.out.println("Inserisci il domicilio:");
                domicilio = pScanner.nextLine();
                System.out.println("Seleziona ruolo:");
                ruolo = pScanner.next();
            }

            // Tentativo di registrazione dell'utente

            // if(registrazione avvenuta con successo) (da gestire poi con enum)

            if (checkOk) {
                switch (ruolo) {
                    case "Cliente":
                        break;
                    case "Bigliettaio":
                        break;
                    case "Proiezionista":
                        break;
                }
            }
        } while (!checkOk);

    }

    private static void pMenuGuest() {
        int inputParsato = 0;
        Boolean checkOk = true;

        System.out.println("\n\n===== GUEST =====");
        System.out.println("Cosa vuoi fare?");

        do {
            checkOk = true;
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Visualizza proiezione");
            System.out.println("3. Registrati");

            if(checkOk) inputParsato = pLeggiIntero();

            if (checkOk) {
                switch (inputParsato) {
                    case 1:
                        // Cerca proiezioni
                        break;
                    case 2:
                        // Visualizza proiezione
                        break;
                    case 3:
                        // Registrati
                        MenuManager.pRegistrazione();
                        break;
                    default:
                        System.out.println("Scelta non valida");
                        checkOk = !checkOk;
                        break;
                }
            }
        } while (!checkOk);

    }

    private static void pMenucliente() {
        Boolean checkOk = true;
        int inputParsato = 0;

        System.out.println("\n\n===== CLIENTE =====");
        System.out.println("Cosa vuoi fare?");

        do {
            checkOk = true;
            System.out.println("1. Visualizza prenotazione");
            System.out.println("2. Effettua prenotazione");
            System.out.println("3. Modifica prenotazione");
            System.out.println("4. Elimina prenotazione");

            if(checkOk) inputParsato = pLeggiIntero();

            if (checkOk) {
                switch (inputParsato) {
                    case 1:
                        // Visualizza prenotazione
                        break;
                    case 2:
                        // Effettua prenotazione
                        break;
                    case 3:
                        // Modifica prenotazione
                        break;
                    case 4:
                        // Elimina prenotazione
                        break;
                    default:
                        System.out.println("Scelta non valida");
                        checkOk = !checkOk;
                        break;
                }
            }
        } while (!checkOk);
    }

    // #region Metodi support

    private static int pLeggiIntero() {
        boolean checkOk = true;
        int scelta = 0;

        do {
            checkOk = true;
            if (pScanner.hasNextInt()) {
                scelta = pScanner.nextInt();
            } else {
                System.out.println("Input non valido. Inserisci un numero.");
                pScanner.next(); // Consuma l'input non valido
                checkOk = !checkOk;
            }
        } while (!checkOk);

        return scelta;
    }

    // #endregion

    // #endregion

}
