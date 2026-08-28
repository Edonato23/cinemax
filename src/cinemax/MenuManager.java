package cinemax;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Gestisce i menu dell'applicazione Cinemax e coordina le operazioni su
 * utenti, proiezioni e prenotazioni.
 */
public class MenuManager {
    /** Scanner utilizzato per leggere i dati inseriti dall'utente. */
    private final Scanner pScanner;

    /** Gestore utilizzato per caricare e salvare i dati su file. */
    private FileManager pFileManager;

    /** Elenco degli utenti registrati. */
    private final List<Utente> pUtenti = new ArrayList<>();

    /** Elenco delle proiezioni presenti nel cinema. */
    private final List<Proiezione> pProiezioni = new ArrayList<>();

    /** Elenco delle prenotazioni effettuate. */
    private final List<Prenotazione> pPrenotazioni = new ArrayList<>();

    private int nextUserId = 1;
    private int nextProjectonId = 1;
    private int nextReservationId = 1;

    /**
     * Crea il gestore dei menu e carica i dati persistiti.
     *
     * @param fileManager gestore dei file utilizzato dall'applicazione
     * @throws IOException se si verifica un errore durante il caricamento dei dati
     */
    public MenuManager(FileManager fileManager) throws IOException {
        pScanner = new Scanner(System.in);
        this.pFileManager = fileManager;
        this.pCaricaDati();
    }

    /**
     * Avvia il menu principale dell'applicazione.
     *
     * @throws IOException se si verifica un errore durante il salvataggio dei dati
     */
    public void Menu() throws IOException {
        boolean esecuzione = true;
        this.pStampaTestata();

        while (esecuzione) {
            System.out.println("\nCosa vuoi fare?");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("3. Continua come ospite");
            System.out.println("0. Esci");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> this.pLogin();
                case 2 -> this.pRegistraCliente();
                case 3 -> this.pGuest();
                case 0 -> esecuzione = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }

        System.out.println("\nGrazie per aver scelto Cinemax. Arrivederci!");
        pScanner.close();
    }

    // #region Gestione prenotazioni

    /**
     * Modifica una prenotazione appartenente all'elenco fornito.
     *
     * @param prenotazioni prenotazioni disponibili per la modifica
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pModificaPrenotazione(List<Prenotazione> prenotazioni) throws IOException {
        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai prenotazioni da modificare.");
            return;
        }

        prenotazioni.stream()
                .sorted(Comparator.comparingInt(Prenotazione::getIdPrenotazione))
                .forEach(prenotazione -> System.out.println(prenotazione.toString()));

        int idPrenotazione = this.pLeggiIntero("ID della prenotazione da modificare: ", 1, Integer.MAX_VALUE);
        Prenotazione prenotazione = this.pCercaPrenotazionePropria(idPrenotazione, prenotazioni);

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

    /**
     * Elimina una prenotazione appartenente all'elenco fornito.
     *
     * @param prenotazioni prenotazioni disponibili per l'eliminazione
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pEliminaPrenotazione(List<Prenotazione> prenotazioni) throws IOException {
        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai prenotazioni attive da eliminare.");
            return;
        }

        prenotazioni.forEach(prenotazione -> System.out.println(prenotazione.toString()));

        int idPrenotazione = this.pLeggiIntero("ID della prenotazione da eliminare: ", 1, Integer.MAX_VALUE);
        Prenotazione prenotazione = this.pCercaPrenotazionePropria(idPrenotazione, prenotazioni);

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

    // #endregion

    // #region Gestione proiezioni

    /**
     * Verifica se una proiezione si sovrappone a una proiezione esistente.
     *
     * @param nuovaInizio data e ora di inizio della nuova proiezione
     * @param nuovaDurataMinuti durata della nuova proiezione in minuti
     * @param sorgente proiezioni con cui effettuare il confronto
     * @return {@code true} se esiste una sovrapposizione, altrimenti {@code false}
     */
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

    /**
     * Acquisisce i dati e aggiunge una nuova proiezione.
     *
     * @throws IOException se si verifica un errore durante il salvataggio
     */
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
        String regista = this.pLeggiNome("Regista del film: ");
        int anno = this.pLeggiIntero("Anno del film: ", 1800, LocalDate.now().getYear());
        int etaMinima = this.pLeggiIntero("Età minima per il film: ", 0, 21);
        double prezzoBiglietto = this.pLeggiDouble("Prezzo del biglietto: ", 0.0, Double.MAX_VALUE);

        this.pProiezioni.add(new Proiezione(this.nextProjectonId++, dataOraProiezione, film, genere, regista, anno,
                durataMinuti, etaMinima,
                prezzoBiglietto));

        this.pProiezioni.sort(Comparator.comparingInt(Proiezione::getIdProiezione));

        this.pFileManager.salva(Costanti.NOME_FILE_PROIEZIONI,
                this.pProiezioni,
                Proiezione::toCSV,
                Proiezione.header());
        System.out.println("Proiezione aggiunta con successo.");
    }

    /**
     * Modifica una proiezione esistente dopo aver verificato i vincoli applicativi.
     *
     * @throws IOException se si verifica un errore durante il salvataggio
     */
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
                    proiezioneModificata.setRegista(this.pLeggiNome("Nuovo regista del film: "));
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
                if (this.pProiezioni.get(indice).getIdProiezione() == proiezioneModificata.getIdProiezione()) {
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

    /**
     * Rimuove una proiezione priva di prenotazioni attive.
     *
     * @throws IOException se si verifica un errore durante il salvataggio
     */
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

    // #endregion

    // #region Metodi di controllo input

    /**
     * Legge e valida un nome o un cognome.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @return nome validato
     */
    private String pLeggiNome(String istruzioni) {
        while (true) {
            String nome = this.pLeggiTesto(istruzioni);
            if (nome.matches("\\p{L}+(?:[ '-]\\p{L}+)*")) {
                return nome;
            }
            System.out.println("Inserisci solo lettere, spazi, apostrofi o trattini.");
        }
    }

    /**
     * Legge uno username valido e non ancora utilizzato.
     *
     * @return username validato e disponibile
     */
    private String pLeggiUsername() {
        while (true) {
            String username = this.pLeggiTesto("Username: ");
            if (!username.matches("[A-Za-z0-9._-]{3,20}")) {
                System.out.println("Lo username deve contenere da 3 a 20 caratteri: lettere, numeri, '.', '_' o '-'.");
                continue;
            }
            if (this.pCercaUtente(username) != null) {
                System.out.println("Username già in uso. Scegline un altro.");
                continue;
            }
            return username;
        }
    }

    /**
     * Legge una password conforme ai requisiti e la relativa conferma.
     *
     * @return password confermata
     */
    private String pLeggiPassword() {
        while (true) {
            String password = this.pLeggiTesto("Password (almeno 8 caratteri, maiuscola, minuscola e numero): ");
            if (password.length() < 8
                    || !password.matches(".*[A-Z].*")
                    || !password.matches(".*[a-z].*")
                    || !password.matches(".*[0-9].*")) {
                System.out.println("La password non rispetta i requisiti richiesti.");
                continue;
            }

            String conferma = this.pLeggiTesto("Conferma password: ");
            if (!password.equals(conferma)) {
                System.out.println("Le password non coincidono.");
                continue;
            }
            return password;
        }
    }

    /**
     * Legge una data di nascita opzionale e ne verifica il formato e l'intervallo.
     *
     * @return data di nascita nel formato previsto, oppure una stringa vuota
     */
    private String pLeggiDataNascita() {
        while (true) {
            String dataNascita = this.pLeggiTestoOpzionale(
                    "Data di nascita (dd-MM-yyyy, invio per saltare): ");
            if (dataNascita.isBlank()) {
                return "";
            }

            try {
                LocalDate data = LocalDate.parse(dataNascita, Costanti.FORMATTATORE_DATA);
                LocalDate oggi = LocalDate.now();
                if (data.isAfter(oggi) || data.isBefore(oggi.minusYears(120))) {
                    System.out.println("Inserisci una data compresa tra oggi e 120 anni fa.");
                    continue;
                }
                return dataNascita;
            } catch (DateTimeParseException exception) {
                System.out.println("Formato data non valido. Esempio: 25-12-2000");
            }
        }
    }

    /**
     * Legge un numero intero compreso nell'intervallo indicato.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @param min valore minimo incluso
     * @param max valore massimo incluso
     * @return valore intero validato
     */
    private int pLeggiIntero(String istruzioni, int min, int max) {
        while (true) {

            System.out.print(istruzioni + "\n");
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

    /**
     * Legge un numero decimale compreso nell'intervallo indicato.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @param min valore minimo incluso
     * @param max valore massimo incluso
     * @return valore decimale validato
     */
    private double pLeggiDouble(String istruzioni, double min, double max) {
        while (true) {

            System.out.print(istruzioni + "\n");
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

    /**
     * Legge un testo non vuoto.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @return testo inserito e ripulito dagli spazi iniziali e finali
     */
    private String pLeggiTesto(String istruzioni) {
        while (true) {

            System.out.print(istruzioni + "\n");
            String input = pScanner.nextLine().trim();

            // Controlla che l'input sia valido (non vuoto e non solo spazi bianchi)
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Il campo non può essere vuoto.");
        }
    }

    /**
     * Legge un testo che può essere lasciato vuoto.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @return testo inserito e ripulito dagli spazi iniziali e finali
     */
    private String pLeggiTestoOpzionale(String istruzioni) {
        System.out.print(istruzioni + "\n");
        return pScanner.nextLine().trim();
    }

    /**
     * Legge una risposta affermativa o negativa.
     *
     * @param istruzioni messaggio mostrato all'utente
     * @return {@code true} per una risposta affermativa, altrimenti {@code false}
     */
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

    /**
     * Legge una data e ora future nel formato previsto dall'applicazione.
     *
     * @return data e ora future validate
     */
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

    /**
     * Calcola l'hash BCrypt di una password.
     *
     * @param password password da cifrare
     * @return hash della password
     */
    private String pCifraPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Verifica una password rispetto al suo hash BCrypt.
     *
     * @param password password da verificare
     * @param hash hash atteso
     * @return {@code true} se la password corrisponde all'hash
     */
    private boolean pControllaPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    // #endregion

    // #region Dati e autenticazione

    /**
     * Carica utenti, proiezioni e prenotazioni dai file persistenti.
     *
     * @throws IOException se si verifica un errore durante la lettura dei file
     * @throws IllegalStateException se una prenotazione riferisce una proiezione inesistente
     */
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

    /** Stampa l'intestazione dell'applicazione. */
    private void pStampaTestata() {
        System.out.println("========================================");
        System.out.println("|          BENVENUTO AL CINEMAX        |");
        System.out.println("|     Cinema monosala con 200 posti    |");
        System.out.println("========================================");
    }

    /**
     * Gestisce l'autenticazione e apre il menu associato al ruolo dell'utente.
     *
     * @throws IOException se si verifica un errore durante le operazioni del menu
     */
    private void pLogin() throws IOException {
        System.out.println("\n===== LOGIN =====");
        String username = this.pLeggiTesto("Username: ");
        String password = this.pLeggiTesto("Password: ");

        Utente utente = this.pUtenti.stream()
                .filter(it -> it.getUsername().equalsIgnoreCase(username))
                .filter(it -> this.pControllaPassword(password, it.getPassword()))
                .findFirst()
                .orElse(null);

        if (utente == null) {
            System.out.println("Username o password non validi.");
            return;
        }

        System.out.println("\nAccesso effettuato. Benvenuto/a, " + utente.getNomeCompleto() + "!");

        switch (utente.getRuolo()) {
            case Utente.Ruolo.CLIENTE -> this.pMenuCliente(utente);
            case Utente.Ruolo.PROIEZIONISTA -> this.pMenuProiezionista(utente);
            case Utente.Ruolo.BIGLIETTAIO -> this.pMenuBigliettaio(utente);
            default -> throw new IllegalStateException("Ruolo non gestito.");
        }
    }

    /**
     * Registra un nuovo cliente e salva i dati aggiornati.
     *
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pRegistraCliente() throws IOException {
        System.out.println("\n===== REGISTRAZIONE =====");
        String nome = this.pLeggiNome("Nome: ");
        String cognome = this.pLeggiNome("Cognome: ");
        String username = this.pLeggiUsername();
        String password = this.pLeggiPassword();
        String dataNascita = this.pLeggiDataNascita();

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

    /**
     * Calcola i posti già prenotati per una proiezione.
     *
     * @param idProiezione identificativo della proiezione
     * @return numero di posti occupati
     */
    private int pPostiOccupati(int idProiezione) {
        return this.pPrenotazioni.stream()
                .filter(prenotazione -> prenotazione.getIdProiezione() == idProiezione)
                .mapToInt(Prenotazione::getNumeroPosti)
                .sum();
    }

    // #endregion

    // #region Menu

    /** Apre il menu per gli utenti non autenticati. */
    private void pGuest() throws IOException {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA OSPITE =====");
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Visualizza dettagli proiezione");
            System.out.println("3. Registrati");
            System.out.println("0. Torna al menu principale");

            switch (pLeggiIntero("Scelta: ", 0, 3)) {
                case 1:
                    this.pCercaProiezione();
                    break;
                case 2:
                    this.pVisualizzaProiezione();
                    break;
                case 3:
                    this.pRegistraCliente();
                    open = false;
                    break;
                case 0:
                    open = false;
                    break;
                default:
                    throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    /** Esegue una ricerca combinabile tra le proiezioni disponibili. */
    private void pCercaProiezione() {
        if (this.pProiezioni.isEmpty()) {
            System.out.println("Non ci sono proiezioni.");
            return;
        }

        System.out.println("\n===== CERCA PROIEZIONE =====");
        String titolo = this.pLeggiTestoOpzionale("Titolo (anche parziale, invio per saltare): ").toLowerCase();
        String genere = this.pLeggiTestoOpzionale("Genere (invio per saltare): ").toLowerCase();
        String dataInizioStringa = this.pLeggiTestoOpzionale(
                "Data e ora iniziale (dd/MM/yyyy HH:mm, invio per saltare): ");
        String dataFineStringa = this.pLeggiTestoOpzionale(
                "Data e ora finale (dd/MM/yyyy HH:mm, invio per saltare): ");
        String prezzoMinimoStringa = this.pLeggiTestoOpzionale("Prezzo minimo (invio per saltare): ");
        String prezzoMassimoStringa = this.pLeggiTestoOpzionale("Prezzo massimo (invio per saltare): ");

        LocalDateTime dataInizio = null;
        LocalDateTime dataFine = null;
        Double prezzoMinimo = null;
        Double prezzoMassimo = null;

        try {
            if (!dataInizioStringa.isBlank()) {
                dataInizio = LocalDateTime.parse(dataInizioStringa, Costanti.FORMATTATORE_DATA_ORA);
            }
            if (!dataFineStringa.isBlank()) {
                dataFine = LocalDateTime.parse(dataFineStringa, Costanti.FORMATTATORE_DATA_ORA);
            }
            if (!prezzoMinimoStringa.isBlank()) {
                prezzoMinimo = Double.parseDouble(prezzoMinimoStringa);
            }
            if (!prezzoMassimoStringa.isBlank()) {
                prezzoMassimo = Double.parseDouble(prezzoMassimoStringa);
            }
        } catch (DateTimeParseException | NumberFormatException exception) {
            System.out.println("Uno o più filtri non sono validi.");
            return;
        }

        if ((prezzoMinimo != null && prezzoMinimo < 0)
                || (prezzoMassimo != null && prezzoMassimo < 0)
                || (prezzoMinimo != null && prezzoMassimo != null && prezzoMinimo > prezzoMassimo)) {
            System.out.println("Intervallo di prezzi non valido.");
            return;
        }

        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
            System.out.println("Intervallo di date non valido.");
            return;
        }

        LocalDateTime inizioFiltro = dataInizio;
        LocalDateTime fineFiltro = dataFine;
        Double minimoFiltro = prezzoMinimo;
        Double massimoFiltro = prezzoMassimo;

        List<Proiezione> risultati = this.pProiezioni.stream()
                .filter(proiezione -> proiezione.getDataOraProiezione().isAfter(LocalDateTime.now()))
                .filter(proiezione -> titolo.isBlank()
                        || proiezione.getTitoloFilm().toLowerCase().contains(titolo))
                .filter(proiezione -> genere.isBlank()
                        || proiezione.getGenere().toLowerCase().contains(genere))
                .filter(proiezione -> inizioFiltro == null
                        || !proiezione.getDataOraProiezione().isBefore(inizioFiltro))
                .filter(proiezione -> fineFiltro == null
                        || !proiezione.getDataOraProiezione().isAfter(fineFiltro))
                .filter(proiezione -> minimoFiltro == null
                        || proiezione.getPrezzoBiglietto() >= minimoFiltro)
                .filter(proiezione -> massimoFiltro == null
                        || proiezione.getPrezzoBiglietto() <= massimoFiltro)
                .sorted(Comparator.comparing(Proiezione::getDataOraProiezione))
                .toList();

        if (risultati.isEmpty()) {
            System.out.println("Nessuna proiezione trovata.");
            return;
        }

        risultati.forEach(proiezione -> {
            System.out.println(proiezione.toString());
        });
    }

    /**
     * Apre il menu dedicato a un cliente autenticato.
     *
     * @param utente cliente autenticato
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pMenuCliente(Utente utente) throws IOException {
        boolean open = true;
        List<Prenotazione> prenotazioni = this.pCercaPrenotazioniUtente(utente);

        while (open) {
            System.out.println("\n===== AREA CLIENTE =====");
            System.out.println("1. Cerca prenotazione");
            System.out.println("2. Effettua prenotazione");
            System.out.println("3. Modifica prenotazione");
            System.out.println("4. Elimina prenotazione");
            System.out.println("0. Logout");

            switch (this.pLeggiIntero("Scelta: ", 0, 4)) {
                case 1 -> this.pVisualizzaPrenotazione(prenotazioni);
                case 2 -> this.pCreaPrenotazione(utente.getEta(), utente.getIdUtente());
                case 3 -> this.pModificaPrenotazione(prenotazioni);
                case 4 -> this.pEliminaPrenotazione(prenotazioni);
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    /**
     * Apre il menu dedicato a un proiezionista autenticato.
     *
     * @param utente proiezionista autenticato
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pMenuProiezionista(Utente utente) throws IOException {
        boolean open = true;
        while (open) {
            System.out.println("\n===== AREA PROIEZIONISTA =====");
            System.out.println("1. Aggiungi proiezione");
            System.out.println("2. Modifica proiezione");
            System.out.println("3. Rimuovi proiezione");
            System.out.println("0. Logout");

            switch (this.pLeggiIntero("Scelta: ", 0, 3)) {
                case 1 -> this.pAggiungiProiezione();
                case 2 -> this.pModificaProiezione();
                case 3 -> this.pRimuoviProiezione();
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    /**
     * Apre il menu dedicato a un bigliettaio autenticato.
     *
     * @param utente bigliettaio autenticato
     */
    private void pMenuBigliettaio(Utente utente) {
        boolean open = true;

        while (open) {
            System.out.println("\n===== AREA BIGLIETTAIO =====");
            System.out.println("1. Cerca prenotazione");
            System.out.println("2. Visualizza prenotazioni");
            System.out.println("0. Logout");

            switch (this.pLeggiIntero("Scelta: ", 0, 2)) {
                case 1 -> this.pCercaPrenotazione();
                case 2 -> this.pVisualizzaPrenotazione(this.pPrenotazioni);
                case 0 -> open = false;
                default -> throw new IllegalStateException("Scelta non gestita.");
            }
        }
    }

    // #endregion

    // #region Ricerca

    /**
     * Cerca un utente tramite username senza distinguere maiuscole e minuscole.
     *
     * @param username username da cercare
     * @return utente trovato, oppure {@code null}
     */
    private Utente pCercaUtente(String username) {
        return this.pUtenti.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Cerca una proiezione tramite identificativo.
     *
     * @param id identificativo della proiezione
     * @return proiezione trovata, oppure {@code null}
     */
    private Proiezione pCercaProiezione(int id) {
        return this.pProiezioni.stream()
                .filter(proiezione -> proiezione.getIdProiezione() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Cerca una prenotazione all'interno dell'elenco indicato.
     *
     * @param id identificativo della prenotazione
     * @param prenotazioni elenco in cui cercare
     * @return prenotazione trovata, oppure {@code null}
     */
    private Prenotazione pCercaPrenotazionePropria(int id, List<Prenotazione> prenotazioni) {
        return prenotazioni.stream()
                .filter(prenotazione -> prenotazione.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Restituisce le prenotazioni associate a un utente.
     *
     * @param utente utente di cui cercare le prenotazioni
     * @return elenco delle prenotazioni dell'utente
     */
    private List<Prenotazione> pCercaPrenotazioniUtente(Utente utente) {
        return this.pPrenotazioni.stream()
                .filter(prenotazione -> prenotazione.getUtenteId() == utente.getIdUtente())
                .toList();
    }

    /**
     * Visualizza una prenotazione scelta dall'elenco indicato.
     *
     * @param prenotazioni elenco di prenotazioni consultabili
     */
    private void pVisualizzaPrenotazione(List<Prenotazione> prenotazioni) {

        int id = this.pLeggiIntero("Inserisci l'id della prenotazione", 1, Integer.MAX_VALUE);

        Prenotazione prenotazione = this.pCercaPrenotazionePropria(id, this.pPrenotazioni);

        if (prenotazione == null) {
            System.out.println("Prenotazione non trovata.");
            return;
        }

        System.out.println(prenotazione.toString());
    }

    /** Esegue una ricerca combinabile tra le prenotazioni. */
    private void pCercaPrenotazione() {
        if (this.pPrenotazioni.isEmpty()) {
            System.out.println("Non ci sono prenotazioni.");
            return;
        }

        System.out.println("\n===== CERCA PRENOTAZIONE =====");
        String idStringa = this.pLeggiTestoOpzionale("ID prenotazione (invio per saltare): ");
        String nome = this.pLeggiTestoOpzionale("Nome cliente (invio per saltare): ").toLowerCase();
        String cognome = this.pLeggiTestoOpzionale("Cognome cliente (invio per saltare): ").toLowerCase();
        String titolo = this.pLeggiTestoOpzionale("Titolo film (anche parziale, invio per saltare): ")
                .toLowerCase();
        String dataInizioStringa = this.pLeggiTestoOpzionale(
                "Data iniziale (dd-MM-yyyy HH:mm, invio per saltare): ");
        String dataFineStringa = this.pLeggiTestoOpzionale(
                "Data finale (dd-MM-yyyy HH:mm, invio per saltare): ");

        Integer id = null;
        LocalDateTime dataInizio = null;
        LocalDateTime dataFine = null;

        try {
            if (!idStringa.isBlank()) {
                id = Integer.parseInt(idStringa);
            }
            if (!dataInizioStringa.isBlank()) {
                dataInizio = LocalDateTime.parse(dataInizioStringa, Costanti.FORMATTATORE_DATA_ORA);
            }
            if (!dataFineStringa.isBlank()) {
                dataFine = LocalDateTime.parse(dataFineStringa, Costanti.FORMATTATORE_DATA_ORA);
            }
        } catch (NumberFormatException | DateTimeParseException exception) {
            System.out.println("Uno o più filtri non sono validi.");
            return;
        }

        if (id != null && id <= 0) {
            System.out.println("L'ID della prenotazione deve essere maggiore di zero.");
            return;
        }

        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
            System.out.println("Intervallo di date non valido.");
            return;
        }

        Integer idFiltro = id;
        LocalDateTime inizioFiltro = dataInizio;
        LocalDateTime fineFiltro = dataFine;

        List<Prenotazione> risultati = this.pPrenotazioni.stream()
                .filter(prenotazione -> idFiltro == null
                        || prenotazione.getIdPrenotazione() == idFiltro)
                .filter(prenotazione -> {
                    Utente cliente = this.pUtenti.stream()
                            .filter(utente -> utente.getIdUtente() == prenotazione.getUtenteId())
                            .findFirst()
                            .orElse(null);
                    return cliente != null
                            && (nome.isBlank() || cliente.getNome().toLowerCase().contains(nome))
                            && (cognome.isBlank() || cliente.getCognome().toLowerCase().contains(cognome));
                })
                .filter(prenotazione -> {
                    Proiezione proiezione = this.pCercaProiezione(prenotazione.getIdProiezione());
                    return proiezione != null
                        && (titolo.isBlank()
                            || proiezione.getTitoloFilm().toLowerCase().contains(titolo));
                })
                .filter(prenotazione -> inizioFiltro == null
                    || !this.pCercaProiezione(prenotazione.getIdProiezione()).getDataOraProiezione()
                        .isBefore(inizioFiltro))
                .filter(prenotazione -> fineFiltro == null
                    || !this.pCercaProiezione(prenotazione.getIdProiezione()).getDataOraProiezione()
                        .isAfter(fineFiltro))
                .sorted(Comparator.comparingInt(Prenotazione::getIdPrenotazione))
                .toList();

        if (risultati.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata.");
            return;
        }

        risultati.forEach(prenotazione -> System.out.println(prenotazione));
    }

    // #endregion

    // #region Visualizzazione

    /** Visualizza tutte le proiezioni future ordinate per data. */
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

    /** Visualizza i dettagli di una proiezione scelta dall'utente. */
    private void pVisualizzaProiezione() {
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

        System.out.println("\n" + proiezione.toString());
        System.out.println("Film: " + proiezione.getInfoFilm());
        System.out.println("Posti disponibili: " + (200 - this.pPostiOccupati(id)) + "\n");
    }

    // #endregion

    // #region Creazione

    /**
     * Crea una prenotazione per l'utente indicato.
     *
     * @param eta età dell'utente
     * @param id identificativo dell'utente
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    private void pCreaPrenotazione(int eta, int id) throws IOException {
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

        if (eta < proiezione.getEtaMinima()) {
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
                String codiceUnivoco = this.pGeneraCodiceUnivoco(id, proiezione);
                Prenotazione prenotazione = new Prenotazione(this.nextReservationId++, id,
                        proiezione.getIdProiezione(),
                    postiDaPrenotare, codiceUnivoco);
                prenotazione.setProiezione(proiezione);
                this.pPrenotazioni.add(prenotazione);

                this.pPrenotazioni.sort(Comparator.comparingInt(Prenotazione::getIdPrenotazione));

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

    // #region Codice prenotazione

    /**
     * Genera un codice univoco breve per una prenotazione.
     *
     * @param idUtente identificativo dell'utente
     * @param proiezione proiezione prenotata
     * @return codice esadecimale di otto caratteri
     * @throws IllegalStateException se l'algoritmo SHA-256 non è disponibile
     */
    private String pGeneraCodiceUnivoco(int idUtente, Proiezione proiezione) {
        String codiceBase = Instant.now().toString()
                + proiezione.getDataOraProiezione()
                + idUtente
                + proiezione.getIdProiezione();

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(codiceBase.getBytes(StandardCharsets.UTF_8));
            StringBuilder codice = new StringBuilder(8);
            for (int indice = 0; indice < 4; indice++) {
                codice.append(String.format("%02X", digest[indice]));
            }

            String codiceGenerato = codice.toString();
            boolean codiceGiaPresente = this.pPrenotazioni.stream()
                    .anyMatch(prenotazione -> codiceGenerato.equals(prenotazione.getCodiceUnivoco()));
            if (codiceGiaPresente) {
                return this.pGeneraCodiceUnivoco(idUtente, proiezione);
            }
            return codiceGenerato;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Impossibile generare il codice univoco.", exception);
        }
    }

    // #endregion
}