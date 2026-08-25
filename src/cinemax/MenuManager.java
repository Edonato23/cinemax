package cinemax;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class MenuManager {
    private final Scanner pScanner;
    private FileManager pFileManager;

    private final List<Utente> pUtenti = new ArrayList<>();
    private final List<Proiezione> pProiezioni = new ArrayList<>();
    private final List<Prenotazione> pPrenotazioni = new ArrayList<>();

    private int nextUserId = 1;
    private int nextProjectonId = 1;
    private int nextReservationId = 1;

    public MenuManager(FileManager fileManager) throws IOException {
        pScanner = new Scanner(System.in);
        this.pFileManager = fileManager;
        this.pCaricaDati();
    }

    public void Menu() throws IOException {
        boolean esecuzione = true;
        pStampaTestata();

        while (esecuzione) {
            System.out.println("\nCosa vuoi fare?");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("3. Continua come ospite");
            System.out.println("0. Esci");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pLogin();
                case 2 -> pRegistrazione();
                case 3 -> pGuest();
                case 0 -> esecuzione = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }

        System.out.println("\nGrazie per aver scelto Cinemax. Arrivederci!");
        pScanner.close();
    }

    private void pModificaPrenotazione(Utente utente) throws IOException {
        List<Prenotazione> prenotazioni = this.pCercaPrenotazioniUtente(utente);

        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai prenotazioni da modificare.");
            return;
        }

        prenotazioni.stream()
            .sorted(Comparator.comparingInt(Prenotazione::getIdPrenotazione))
            .forEach(prenotazione -> System.out.println(prenotazione.toString()));

        int idPrenotazione = this.pLeggiIntero("ID della prenotazione da modificare: ", 1, Integer.MAX_VALUE);
        Prenotazione prenotazione = this.pCercaPrenotazionePropria(utente, idPrenotazione, prenotazioni);

        if (prenotazione == null) {
            System.out.println("Prenotazione non trovata.");
            return;
        }

        Proiezione proiezioneCorrente = this.pCercaProiezione(prenotazione.getIdProiezione());
        if (proiezioneCorrente == null) {
            System.out.println("La proiezione associata non esiste più.");
            return;
        }

        if (!proiezioneCorrente.getDataOraProiezione().isAfter(LocalDateTime.now())) {
            System.out.println("Non puoi modificare una prenotazione per una proiezione già avvenuta.");
            return;
        }

        Prenotazione prenotazioneModificata = new Prenotazione(prenotazione);
        prenotazioneModificata.setProiezione(proiezioneCorrente);
        boolean continuaModifica = true;

        do {
            System.out.println("\nCosa vuoi modificare?");
            System.out.println("1. Proiezione");
            System.out.println("2. Numero di posti");
            System.out.println("3. Fine");
            System.out.println("0. Annulla");

            int scelta = this.pLeggiIntero("Scelta: ", 0, 3);

            switch (scelta) {
                case 1: {
                    this.pMostraProiezioni();
                    int idNuovaProiezione = this.pLeggiIntero("ID della nuova proiezione: ", 1, Integer.MAX_VALUE);
                    Proiezione nuovaProiezione = this.pCercaProiezione(idNuovaProiezione);

                    if (nuovaProiezione == null) {
                        System.out.println("Proiezione non trovata.");
                        break;
                    }

                    if (!nuovaProiezione.getDataOraProiezione().isAfter(LocalDateTime.now())) {
                        System.out.println("Non puoi prenotare una proiezione già avvenuta o in corso.");
                        break;
                    }

                    prenotazioneModificata.setProiezione(nuovaProiezione);
                    proiezioneCorrente = nuovaProiezione;
                    System.out.println("Proiezione aggiornata.");
                }
                case 2: {
                    int postiDisponibili = 200 - this.pPostiOccupati(proiezioneCorrente.getIdProiezione());
                    if (prenotazioneModificata.getIdProiezione() == proiezioneCorrente.getIdProiezione()) {
                        postiDisponibili += prenotazioneModificata.getNumeroPosti();
                    }

                    if (postiDisponibili <= 0) {
                        System.out.println("Non ci sono posti disponibili per questa proiezione.");
                        break;
                    }

                    int postiNuovi = this.pLeggiIntero("Nuovo numero di posti (max: " + postiDisponibili + "): ", 1,
                            postiDisponibili);
                    prenotazioneModificata.setNumeroPosti(postiNuovi);
                    System.out.println("Numero di posti aggiornato.");

                    break;
                }
                case 3:
                    continuaModifica = false;
                    break;
                case 0: {
                    System.out.println("Modifica annullata.");
                    return;
                }
                default:
                    throw new IllegalStateException("Scelta non gestita.");
            }
        } while (continuaModifica);

        try {
            int index = this.pPrenotazioni.indexOf(prenotazione);
            if (index >= 0) {
                this.pPrenotazioni.set(index, prenotazioneModificata);
            }

            System.out.println("Prenotazione modificata con successo.");

            this.pPrenotazioni.sort(Comparator.comparingInt(Prenotazione::getIdPrenotazione));

            this.pFileManager.salva(Costanti.NOME_FILE_PRENOTAZIONI,
                    this.pPrenotazioni,
                    Prenotazione::toCSV,
                    Prenotazione.header());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Modifica non effettuata: " + exception.getMessage());
        }
    }

    private void pEliminaPrenotazione(Utente utente) throws IOException {
        List<Prenotazione> prenotazioni = this.pCercaPrenotazioniUtente(utente);

        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai prenotazioni attive da eliminare.");
            return;
        }

        prenotazioni.forEach(prenotazione -> System.out.println(prenotazione.toString()));

        int idPrenotazione = this.pLeggiIntero("ID della prenotazione da eliminare: ", 1, Integer.MAX_VALUE);
        Prenotazione prenotazione = this.pCercaPrenotazionePropria(utente, idPrenotazione, prenotazioni);

        if (prenotazione == null) {
            System.out.println("Prenotazione non trovata.");
            return;
        }

        Proiezione proiezione = this.pCercaProiezione(prenotazione.getIdProiezione());
        if (proiezione == null) {
            System.out.println("La proiezione associata non esiste più.");
            return;
        }

        if (!LocalDateTime.now().isAfter(proiezione.getDataOraProiezione())) {
            System.out.println("Non è possibile eliminare la prenotazione.");
            return;
        }

        if (this.pSiNo("Confermi l'eliminazione? (s/n): ")) {
            this.pPrenotazioni.remove(prenotazione);

            this.pPrenotazioni.sort(Comparator.comparingInt(Prenotazione::getIdPrenotazione));

            this.pFileManager.salva(Costanti.NOME_FILE_PRENOTAZIONI,
                    this.pPrenotazioni,
                    Prenotazione::toCSV,
                    Prenotazione.header());
            System.out.println("Prenotazione eliminata. I posti sono nuovamente disponibili.");
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    private boolean pHaSovrapposizione(LocalDateTime nuovaInizio, int nuovaDurataMinuti, List<Proiezione> sorgente) {
        LocalDateTime nuovaFine = nuovaInizio.plusMinutes(nuovaDurataMinuti);

        for (Proiezione proiezioneEsistente : sorgente) {
            LocalDateTime inizioEsistente = proiezioneEsistente.getDataOraProiezione();
            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(proiezioneEsistente.getDurataMinuti());

            if (!nuovaFine.isBefore(inizioEsistente) && !nuovaInizio.isAfter(fineEsistente)) {
                return true;
            }
        }

        return false;
    }

    private void pAggiungiProiezione() throws IOException {
        System.out.println("\n===== NUOVA PROIEZIONE =====");
        LocalDateTime dataOraProiezione = this.pLeggiDataFutura();
        int durataMinuti = this.pLeggiIntero("Durata del film in minuti (max 600): ", 1, 600);

        if (this.pHaSovrapposizione(dataOraProiezione, durataMinuti, this.pProiezioni)) {
            System.out.println("Impossibile aggiungere la proiezione a causa di sovrapposizione.");
            return;
        }

        String film = this.pLeggiTesto("Titolo del film: ");
        String genere = this.pLeggiTesto("Genere del film: ");
        String regista = this.pLeggiTesto("Regista del film: ");
        int anno = this.pLeggiIntero("Anno del film: ", 1800, LocalDate.now().getYear());
        int etaMinima = this.pLeggiIntero("Età minima per il film: ", 0, 21);
        double prezzoBiglietto = this.pLeggiDouble("Prezzo del biglietto: ", 0.0, Double.MAX_VALUE);

        this.pProiezioni.add(new Proiezione(this.nextProjectonId++, dataOraProiezione, film, genere, regista, anno, durataMinuti, etaMinima,
                prezzoBiglietto));

        this.pProiezioni.sort(Comparator.comparingInt(Proiezione::getIdProiezione));

        this.pFileManager.salva(Costanti.NOME_FILE_PROIEZIONI,
            this.pProiezioni,
            Proiezione::toCSV,
            Proiezione.header());
        System.out.println("Proiezione aggiunta con successo.");
    }

    private void pModificaProiezione() throws IOException {
        this.pMostraProiezioni();
        boolean continuaModifica = true;

        int id = this.pLeggiIntero("ID della proiezione da modificare: ", 1, Integer.MAX_VALUE);
        Proiezione proiezione = this.pCercaProiezione(id);

        if (proiezione == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        Proiezione proiezioneModificata = new Proiezione(proiezione);

        if (this.pPostiOccupati(id) > 0) {
            System.out.println("Non puoi modificare questa proiezione: esistono prenotazioni attive.");
            return;
        }

        do {
            System.out.println("\nCosa vuoi modificare?");
            System.out.println("1. Data e ora della proiezione");
            System.out.println("2. Durata del film");
            System.out.println("3. Titolo del film");
            System.out.println("4. Genere del film");
            System.out.println("5. Regista del film");
            System.out.println("6. Anno del film");
            System.out.println("7. Età minima per il film");
            System.out.println("8. Prezzo del biglietto");
            System.out.println("9. Fine");
            System.out.println("0. Annulla");

            int scelta = this.pLeggiIntero("Scelta: ", 0, 9);

            switch (scelta) {
                case 1:
                    // Modifica data e ora della proiezione
                    proiezioneModificata.setDataOraProiezione(this.pLeggiDataFutura());
                    break;
                case 2:
                    // Modifica durata del film
                    proiezioneModificata
                            .setDurataMinuti(this.pLeggiIntero("Durata del film in minuti (max 600): ", 1, 600));
                    break;
                case 3:
                    // Modifica titolo del film
                    proiezioneModificata.setTitolo(this.pLeggiTesto("Nuovo titolo del film: "));
                    break;
                case 4:
                    // Modifica genere del film
                    proiezioneModificata.setGenere(this.pLeggiTesto("Nuovo genere del film: "));
                    break;
                case 5:
                    // Modifica regista del film
                    proiezioneModificata.setRegista(this.pLeggiTesto("Nuovo regista del film: "));
                    break;
                case 6:
                    // Modifica anno del film
                    proiezioneModificata
                            .setAnno(this.pLeggiIntero("Nuovo anno del film: ", 1800, LocalDateTime.now().getYear()));
                    break;
                case 7:
                    // Modifica età minima per il film
                    proiezioneModificata.setEtaMinima(this.pLeggiIntero("Nuova età minima per il film: ", 0, 21));
                    break;
                case 8:
                    // Modifica prezzo del biglietto
                    proiezioneModificata
                            .setPrezzoBiglietto(this.pLeggiDouble("Nuovo prezzo del biglietto: ", 0, Double.MAX_VALUE));
                    break;
                case 9:
                    continuaModifica = false;
                    break;
                case 0:
                    System.out.println("Modifica annullata.");
                    return;
                default:
                    throw new IllegalStateException("Scelta non gestita.");
            }
        } while (continuaModifica);

        // Validazione modifiche e salvataggio
        List<Proiezione> proiezioniSenzaQuellaModificata = this.pProiezioni.stream()
                .filter(it -> it.getIdProiezione() != proiezioneModificata.getIdProiezione())
                .toList();

        if (this.pHaSovrapposizione(proiezioneModificata.getDataOraProiezione(),
                proiezioneModificata.getDurataMinuti(),
                proiezioniSenzaQuellaModificata)) {
            System.out.println("Impossibile modificare la proiezione a causa di sovrapposizione.");
            return;
        }

        try {
            int index = -1;
            for (int indice = 0; indice < this.pProiezioni.size(); indice++) {
                if (this.pProiezioni.get(indice).getIdProiezione()
                        == proiezioneModificata.getIdProiezione()) {
                    index = indice;
                    break;
                }
            }
            if (index >= 0) {
                this.pProiezioni.set(index, proiezioneModificata);
            }

            System.out.println("Proiezione modificata con successo.");

            this.pProiezioni.sort(Comparator.comparingInt(Proiezione::getIdProiezione));

            this.pFileManager.salva(Costanti.NOME_FILE_PROIEZIONI,
                    this.pProiezioni,
                    Proiezione::toCSV,
                    Proiezione.header());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Modifica non effettuata: " + exception.getMessage());
        }
    }

    private void pRimuoviProiezione() throws IOException {
        this.pMostraProiezioni();
        int id = this.pLeggiIntero("ID della proiezione da rimuovere: ", 1, Integer.MAX_VALUE);
        Proiezione proiezione = this.pCercaProiezione(id);

        if (proiezione == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        if (this.pPostiOccupati(id) > 0) {
            System.out.println("Non puoi eliminare questa proiezione: esistono prenotazioni attive.");
            return;
        }

        if (this.pSiNo("Confermi l'eliminazione? (s/n): ")) {
            this.pProiezioni.remove(proiezione);
            System.out.println("Proiezione rimossa.");

            this.pProiezioni.sort(Comparator.comparingInt(Proiezione::getIdProiezione));

            this.pFileManager.salva(Costanti.NOME_FILE_PROIEZIONI,
                    this.pProiezioni,
                    Proiezione::toCSV,
                    Proiezione.header());
            System.out.println("Proiezione eliminata. I posti sono nuovamente disponibili.");
        } else {
            System.out.println("Operazione annullata.");
        }
    }

    // #region Metodi di controllo input

    private int pLeggiIntero(String istruzioni, int min, int max) {
        while (true) {

            System.out.print(istruzioni);
            String input = pScanner.nextLine().trim();

            try {
                int inputParsato = Integer.parseInt(input);

                // Controlla che l'input sia compreso tra min e max
                if (inputParsato < min || inputParsato > max) {
                    System.out.println("Inserisci un numero tra " + min + " e " + max + ".");
                    continue;
                }

                return inputParsato;
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }
    }

    private double pLeggiDouble(String istruzioni, double min, double max) {
        while (true) {

            System.out.print(istruzioni);
            String input = pScanner.nextLine().trim();

            try {
                double inputParsato = Double.parseDouble(input);

                // Controlla che l'input sia compreso tra min e max
                if (inputParsato < min || inputParsato > max) {
                    System.out.println("Inserisci un numero tra " + min + " e " + max + ".");
                    continue;
                }

                return inputParsato;
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }
    }

    private String pLeggiTesto(String istruzioni) {
        while (true) {

            System.out.print(istruzioni);
            String input = pScanner.nextLine().trim();

            // Controlla che l'input sia valido (non vuoto e non solo spazi bianchi)
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Il campo non può essere vuoto.");
        }
    }

    private String pLeggiTestoOpzionale(String istruzioni) {
        System.out.print(istruzioni);
        return pScanner.nextLine().trim();
    }

    private boolean pSiNo(String istruzioni) {
        while (true) {
            String risposta = pLeggiTesto(istruzioni).toLowerCase();

            if (risposta.equals("s")
                    ||
                    risposta.equals("si")
                    ||
                    risposta.equals("sì")) {
                return true;
            }

            if (risposta.equals("n")
                    ||
                    risposta.equals("no")) {
                return false;
            }

            System.out.println("Rispondi con s oppure n.");
        }
    }

    private LocalDateTime pLeggiDataFutura() {
        while (true) {
            String input = pLeggiTesto("Data e ora (dd/MM/yyyy HH:mm): ");

            try {
                LocalDateTime data = LocalDateTime.parse(input, Costanti.FORMATTATORE_DATA_ORA);
                if (!data.isAfter(LocalDateTime.now())) {
                    System.out.println("Inserisci una data e ora future.");
                    continue;
                }

                return data;
            } catch (DateTimeParseException exception) {
                System.out.println("Formato non valido. Esempio: 25/12/2026 20:30");
            }
        }
    }

    private boolean pDataValida(String dataStringa) {
        try {
            LocalDate.parse(dataStringa, Costanti.FORMATTATORE_DATA);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private String pCifraPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private boolean pControllaPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    // #endregion

    // #region Metodi privati

    private void pCaricaDati() throws IOException {
        this.pUtenti.addAll(this.pFileManager.carica(Costanti.NOME_FILE_UTENTI, Utente::fromCSV));

        this.pProiezioni.addAll(this.pFileManager.carica(Costanti.NOME_FILE_PROIEZIONI, Proiezione::fromCSV));

        this.pPrenotazioni.addAll(this.pFileManager.carica(Costanti.NOME_FILE_PRENOTAZIONI, Prenotazione::fromCSV));

        this.nextUserId = this.pUtenti.stream()
            .mapToInt(Utente::getIdUtente)
            .max()
            .orElse(0) + 1;
        this.nextProjectonId = this.pProiezioni.stream()
            .mapToInt(Proiezione::getIdProiezione)
            .max()
            .orElse(0) + 1;
        this.nextReservationId = this.pPrenotazioni.stream()
            .mapToInt(Prenotazione::getIdPrenotazione)
            .max()
            .orElse(0) + 1;

        this.pPrenotazioni.forEach(prenotazione -> {
            Proiezione proiezione = this.pCercaProiezione(prenotazione.getIdProiezione());
            if (proiezione == null) {
                throw new IllegalStateException(
                        "La prenotazione " + prenotazione.getIdPrenotazione()
                                + " riferisce una proiezione inesistente: " + prenotazione.getIdProiezione());
            }
            prenotazione.setProiezione(proiezione);
        });
    }

    private void pStampaTestata() {
        System.out.println("========================================");
        System.out.println("|          BENVENUTO AL CINEMAX        |");
        System.out.println("|     Cinema monosala con 200 posti    |");
        System.out.println("========================================");
    }

    private void pLogin() throws IOException {
        System.out.println("\n===== LOGIN =====");
        String username = this.pLeggiTesto("Username: ");
        String password = this.pLeggiTesto("Password: ");

        Utente utente = this.pUtenti.stream()
                .filter(it -> it.getUsername().equalsIgnoreCase(username))
                .filter(it -> pControllaPassword(password, it.getPassword()))
                .findFirst()
                .orElse(null);

        if (utente == null) {
            System.out.println("Username o password non validi.");
            return;
        }

        System.out.println("\nAccesso effettuato. Benvenuto/a, " + utente.getNomeCompleto() + "!");

        switch (utente.getRuolo()) {
            case Utente.Ruolo.CLIENTE -> pMenuCliente(utente);
            case Utente.Ruolo.PROIEZIONISTA -> pMenuProiezionista(utente);
            case Utente.Ruolo.BIGLIETTAIO -> pMenuBigliettaio(utente);
            default -> throw new IllegalStateException("Ruolo non gestito.");
        }
    }

    // TODO: Controlli di validità
    private void pRegistrazione() throws IOException {
        boolean dataValida;
        String dataNascita = "";

        System.out.println("\n===== REGISTRAZIONE =====");
        String nome = this.pLeggiTesto("Nome: ");
        String cognome = this.pLeggiTesto("Cognome: ");
        String username;

        while (true) {
            username = this.pLeggiTesto("Username: ");
            if (this.pCercaUtente(username) == null) {
                break;
            }
            System.out.println("Username già in uso. Scegline un altro.");
        }

        String password = this.pLeggiTesto("Password: ");

        do {
            dataValida = true;
            dataNascita = this.pLeggiTestoOpzionale("Data di nascita (dd/MM/yyyy, invio per saltare): ");
            if (!dataNascita.isBlank() && !this.pDataValida(dataNascita)) {
                System.out.println("Formato data non valido: riprova.");
                dataNascita = "";
                dataValida = false;
            }
        } while (!dataValida);

        String domicilio = this.pLeggiTesto("Domicilio: ");

        int ruolo = this.pLeggiIntero(
                "Che ruolo vuoi registrare?\n0. Cliente\n1. Proiezionista\n2. Bigliettaio\nScelta: ",
                0, 2);

        Utente utente = new Utente(
            this.nextUserId++,
                nome,
                cognome,
                username,
                this.pCifraPassword(password),
                domicilio,
                dataNascita,
                ruolo);

        this.pUtenti.add(utente);

        this.pUtenti.sort(Comparator.comparingInt(Utente::getIdUtente));

        this.pFileManager.salva(Costanti.NOME_FILE_UTENTI,
            this.pUtenti,
            Utente::toCSV,
            Utente.header());

        System.out.println("Registrazione completata. Ora puoi effettuare il login.");
    }

    private int pPostiOccupati(int idProiezione) {
        return this.pPrenotazioni.stream()
                .filter(prenotazione -> prenotazione.getIdProiezione() == idProiezione)
                .mapToInt(Prenotazione::getNumeroPosti)
                .sum();
    }

    // #region Menu

    private void pGuest() throws IOException {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA OSPITE =====");
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Visualizza dettagli proiezione");
            System.out.println("3. Registrati");
            System.out.println("0. Torna al menu principale");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pMostraProiezioni();
                case 2 -> pMostraDettagliProiezione();
                case 3 -> pRegistrazione();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuCliente(Utente utente) throws IOException {
        boolean open = true;

        while (open) {
            System.out.println("\n===== AREA CLIENTE =====");
            System.out.println("1. Cerca prenotazione");
            System.out.println("2. Effettua prenotazione");
            System.out.println("3. Modifica prenotazione");
            System.out.println("4. Elimina prenotazione");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 4)) {
                case 1 -> this.pCercaPrenotazione(utente);
                case 2 -> this.pCreaPrenotazione(utente);
                case 3 -> this.pModificaPrenotazione(utente);
                case 4 -> this.pEliminaPrenotazione(utente);
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuProiezionista(Utente utente) throws IOException {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA PROIEZIONISTA =====");
            System.out.println("1. Aggiungi proiezione");
            System.out.println("2. Modifica proiezione");
            System.out.println("3. Rimuovi proiezione");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> pAggiungiProiezione();
                case 2 -> pModificaProiezione();
                case 3 -> pRimuoviProiezione();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    private void pMenuBigliettaio(Utente utente) {
        boolean open = true;

        while (open) {
            System.out.println("\n===== AREA BIGLIETTAIO =====");
            System.out.println("1. Cerca prenotazione");
            System.out.println("2. Visualizza prenotazioni");
            System.out.println("0. Logout");

            switch (pLeggiIntero("Scelta: ", 0, 2)) {
                case 1 -> pCercaPrenotazione();
                case 2 -> pMostraPrenotazioni();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    // #endregion

    // #region Metodi Cerca

    private Utente pCercaUtente(String username) {
        return this.pUtenti.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    private Proiezione pCercaProiezione(int id) {
        return this.pProiezioni.stream()
                .filter(proiezione -> proiezione.getIdProiezione() == id)
                .findFirst()
                .orElse(null);
    }

    private Prenotazione pCercaPrenotazionePropria(Utente utente, int id, List<Prenotazione> prenotazioni) {
        return prenotazioni.stream()
                .filter(prenotazione -> prenotazione.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<Prenotazione> pCercaPrenotazioniUtente(Utente utente) {
        return this.pPrenotazioni.stream()
                .filter(prenotazione -> prenotazione.getUtenteId() == utente.getIdUtente())
                .toList();
    }

    // TODO: Criteri di ricerca
    private void pCercaPrenotazione(Utente utente) {
        this.pMostraPrenotazioni(utente);

        int id = this.pLeggiIntero("Inserisci l'id della prenotazione", 1, Integer.MAX_VALUE);

        Prenotazione prenotazione = this.pCercaPrenotazionePropria(utente, id, this.pPrenotazioni);

        if (prenotazione == null) {
            System.out.println("Prenotazione non trovata.");
            return;
        }

        System.out.println(prenotazione.toString());
    }

    private void pCercaPrenotazione() {
        int id = this.pLeggiIntero("Inserisci l'id della prenotazione", 1, Integer.MAX_VALUE);

        Prenotazione prenotazione = this.pPrenotazioni.stream()
                .filter(it -> it.getIdPrenotazione() == id)
                .findFirst()
                .orElse(null);

        if (prenotazione == null) {
            System.out.println("Prenotazione non trovata.");
            return;
        }

        System.out.println(prenotazione.toString());
    }

    // #endregion

    // #region Metodi Mostra

    private void pMostraProiezioni() {
        System.out.println("\n===== PROIEZIONI DISPONIBILI =====");
        List<Proiezione> proiezioniFuture = this.pProiezioni.stream()
                .filter(it -> it.getDataOraProiezione().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Proiezione::getDataOraProiezione))
                .toList();

        if (proiezioniFuture.isEmpty()) {
            System.out.println("Non ci sono proiezioni disponibili.");
            return;
        }

        proiezioniFuture.forEach(proiezione -> {
            System.out.println(proiezione.toString());
            System.out.println("---------------------");
        });
    }

    private void pMostraDettagliProiezione() {
        if (this.pProiezioni.isEmpty()) {
            System.out.println("Non ci sono proiezioni.");
            return;
        }

        int id = this.pLeggiIntero("Inserisci l'ID della proiezione: ", 1, Integer.MAX_VALUE);
        Proiezione proiezione = this.pCercaProiezione(id);
        if (proiezione == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        System.out.println("\n" + proiezione);
        System.out.println("Film: " + proiezione.getInfoFilm());
        System.out.println("Data e ora: " + proiezione.getDataOraProiezione().format(Costanti.FORMATTATORE_DATA_ORA));
        System.out.println("Prezzo biglietto: " + proiezione.getPrezzoBiglietto() + "€");
        System.out.println("Posti disponibili: " + (200 - this.pPostiOccupati(id)) + "\n");
    }

    private void pMostraPrenotazioni(Utente utente) {
        System.out.println("\n===== LE MIE PRENOTAZIONI =====");
        List<Prenotazione> prenotazioni = this.pCercaPrenotazioniUtente(utente);

        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai ancora effettuato prenotazioni.");
            return;
        }
        prenotazioni.stream()
            .sorted(Comparator.comparingInt(Prenotazione::getIdPrenotazione))
            .forEach(prenotazione -> System.out.println(prenotazione.toString()));
    }

    private void pMostraPrenotazioni() {
        System.out.println("\n===== PRENOTAZIONI =====");
        this.pPrenotazioni.sort(Comparator.comparingInt(Prenotazione::getIdPrenotazione));

        if (this.pPrenotazioni.isEmpty()) {
            System.out.println("Non ci sono prenotazioni.");
            return;
        }

        this.pPrenotazioni.forEach(prenotazione -> System.out.println(prenotazione.toString()));
    }

    // #endregion

    // #region Metodi Crea

    private void pCreaPrenotazione(Utente utente) throws IOException {

        this.pMostraProiezioni();

        if (this.pProiezioni.isEmpty()) {
            System.out.println("Non è possibile effettuare una prenotazione al momento");
            return;
        }

        int idProiezione = this.pLeggiIntero("ID della proiezione da prenotare: ", 1, Integer.MAX_VALUE);
        Proiezione proiezione = this.pCercaProiezione(idProiezione);

        if (proiezione == null || !proiezione.getDataOraProiezione().isAfter(LocalDateTime.now())) {
            System.out.println("Proiezione non trovata o non più disponibile.");
            return;
        }

        if (utente.getEta() < proiezione.getEtaMinima()) {
            System.out.println(
                    "Non puoi prenotare questa proiezione: età minima richiesta " + proiezione.getEtaMinima() + ".");
            return;
        }

        int postiOccupati = this.pPostiOccupati(idProiezione);
        int postiDisponibili = 200 - postiOccupati;

        if (postiDisponibili > 0) {
            int postiDaPrenotare = this.pLeggiIntero("Numero di posti da prenotare (max: " + postiDisponibili + "): ",
                    1,
                    postiDisponibili);

            try {
                Prenotazione prenotazione = new Prenotazione(this.nextReservationId++, utente.getIdUtente(),
                        proiezione.getIdProiezione(),
                        postiDaPrenotare);
                prenotazione.setProiezione(proiezione);
                this.pPrenotazioni.add(prenotazione);

                this.pFileManager.salva(Costanti.NOME_FILE_PRENOTAZIONI,
                        this.pPrenotazioni,
                        Prenotazione::toCSV,
                        Prenotazione.header());

                System.out.println("Prenotazione effettuata con successo!");
                System.out
                        .println("Totale prenotazione: " + (proiezione.getPrezzoBiglietto() * postiDaPrenotare) + "€");
                System.out.println(prenotazione);
            } catch (IllegalArgumentException exception) {
                System.out.println("Prenotazione non effettuata: " + exception.getMessage());
            }
        } else {
            System.out.println("Non ci sono posti disponibili per questa proiezione.");
        }
    }

    // #endregion

    // #endregion
}